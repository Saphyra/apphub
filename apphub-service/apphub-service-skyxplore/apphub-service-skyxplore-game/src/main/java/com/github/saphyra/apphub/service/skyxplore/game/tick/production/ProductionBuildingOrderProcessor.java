package com.github.saphyra.apphub.service.skyxplore.game.tick.production;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuilding;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionData;
import com.github.saphyra.apphub.service.skyxplore.game.common.converter.model.ProductionOrderToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.ProductionOrder;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.Assignment;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.work.AssignCitizenService;
import com.github.saphyra.apphub.service.skyxplore.game.tick.work.AvailableCitizenProvider;
import com.github.saphyra.apphub.service.skyxplore.game.tick.work.MakeCitizenWorkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class ProductionBuildingOrderProcessor {
    private final ProductionBuildingService productionBuildingService;
    private final AllocatedResourceResolver allocatedResourceResolver;
    private final TickCache tickCache;
    private final AvailableCitizenProvider availableCitizenProvider;
    private final AssignCitizenService assignCitizenService;
    private final MakeCitizenWorkService makeCitizenWorkService;
    private final ProductionOrderToModelConverter productionOrderToModelConverter;

    void processOrderByAssignedBuilding(UUID gameId, Planet planet, Building building, ProductionOrder order) {
        //Consuming resources allocated for this specific order
        if (order.getCurrentWorkPoints() == 0) {
            allocatedResourceResolver.resolveAllocations(gameId, planet, order.getProductionOrderId());
        }

        ProductionBuilding productionBuilding = productionBuildingService.get(building.getDataId());
        ProductionData productionData = productionBuilding.getGives().get(order.getDataId());
        int parallelism = Math.min(productionBuilding.getWorkers(), productionData.getConstructionRequirements().getParallelWorkers());

        Map<UUID, Assignment> citizenAssignments = tickCache.get(gameId).getCitizenAssignments();

        for (int i = 0; i < parallelism && order.getCurrentWorkPoints() < order.getRequiredWorkPoints(); i++) {
            Optional<Citizen> maybeUnemployedCitizen = availableCitizenProvider.findMostCapableUnemployedCitizen(citizenAssignments.keySet(), planet.getPopulation().values(), productionData.getRequiredSkill());
            if (maybeUnemployedCitizen.isPresent()) {
                Citizen citizen = maybeUnemployedCitizen.get();
                Assignment assignment = assignCitizenService.assignCitizen(gameId, citizen, building.getBuildingId());

                int completedWork = makeCitizenWorkService.requestWork(gameId, planet.getOwner(), planet.getPlanetId(), assignment, order.getRequiredWorkPoints() - order.getCurrentWorkPoints(), productionData.getRequiredSkill());
                order.setCurrentWorkPoints(order.getCurrentWorkPoints() + completedWork);
            } else {
                break;
            }
        }

        tickCache.get(gameId)
            .getGameItemCache()
            .save(productionOrderToModelConverter.convert(order, gameId));
    }
}

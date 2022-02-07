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
            log.debug("{} has all the necessary resources. Removing them from storage to start work in game {}", order, gameId);
            allocatedResourceResolver.resolveAllocations(gameId, planet, order.getProductionOrderId());
        }

        ProductionBuilding productionBuilding = productionBuildingService.get(building.getDataId());
        log.debug("{} found for dataId {}", productionBuilding, building.getDataId());
        ProductionData productionData = productionBuilding.getGives()
            .get(order.getDataId());
        log.debug("{} found for dataId {}", productionData, order.getDataId());
        int parallelism = Math.min(productionBuilding.getWorkers(), productionData.getConstructionRequirements().getParallelWorkers());

        Map<UUID, Assignment> citizenAssignments = tickCache.get(gameId).getCitizenAssignments();

        for (int i = 0; i < parallelism && order.getCurrentWorkPoints() < order.getRequiredWorkPoints(); i++) {
            log.debug("Employing #{} citizen out of {} in game {}", i + 1, parallelism, gameId);
            Optional<Citizen> maybeUnemployedCitizen = availableCitizenProvider.findMostCapableUnemployedCitizen(
                citizenAssignments,
                planet.getPopulation().values(),
                building.getBuildingId(),
                productionData.getRequiredSkill()
            );
            log.debug("unemployedCitizen: {} in game {}", maybeUnemployedCitizen, gameId);
            if (maybeUnemployedCitizen.isPresent()) {
                Citizen citizen = maybeUnemployedCitizen.get();
                Assignment assignment = Optional.ofNullable(citizenAssignments.get(citizen.getCitizenId()))
                    .orElseGet(() -> assignCitizenService.assignCitizen(gameId, citizen, building.getBuildingId()));
                log.debug("Assignment for order {}: {} in game {}", order, assignment, gameId);

                int requestedWorkPoints = order.getRequiredWorkPoints() - order.getCurrentWorkPoints();
                int completedWork = makeCitizenWorkService.requestWork(
                    gameId,
                    planet.getOwner(),
                    planet.getPlanetId(),
                    assignment,
                    requestedWorkPoints,
                    productionData.getRequiredSkill()
                );
                log.debug("{} work is completed out of {}. Remaining workPoint for assignment: {}", completedWork, requestedWorkPoints, assignment.getWorkPointsLeft());
                order.setCurrentWorkPoints(order.getCurrentWorkPoints() + completedWork);
            } else {
                log.debug("No more citizens can work for building {} in game {}", building, gameId);
                break;
            }
        }

        log.debug("{} after processing in game {}", order, gameId);

        tickCache.get(gameId)
            .getGameItemCache()
            .save(productionOrderToModelConverter.convert(order, gameId));
    }
}

package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.AllBuildingService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingData;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.ConstructionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.consumption.ResourceConsumptionService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.BuildingToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.ConstructionToModelConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpgradeBuildingService {
    private final GameDao gameDao;
    private final AllBuildingService allBuildingService;
    private final ConstructionFactory constructionFactory;
    private final ResourceConsumptionService resourceConsumptionService;
    private final GameDataProxy gameDataProxy;
    private final BuildingToModelConverter buildingToModelConverter;
    private final ConstructionToModelConverter constructionToModelConverter;
    private final SurfaceToResponseConverter surfaceToResponseConverter;

    public SurfaceResponse upgradeBuilding(UUID userId, UUID planetId, UUID buildingId) {
        Game game = gameDao.findByUserIdValidated(userId);
        Planet planet = game
            .getUniverse()
            .findByOwnerAndPlanetIdValidated(userId, planetId);
        Surface surface = planet.getSurfaces()
            .findByBuildingIdValidated(buildingId);
        Building building = surface.getBuilding();

        if (nonNull(building.getConstruction())) {
            throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS, "Construction already in progress on planet " + planetId + " and building " + buildingId);
        }

        BuildingData buildingData = allBuildingService.get(building.getDataId());
        ConstructionRequirements constructionRequirements = buildingData.getConstructionRequirements()
            .get(building.getLevel() + 1);
        if (isNull(constructionRequirements)) {
            throw ExceptionFactory.notLoggedException(
                HttpStatus.FORBIDDEN,
                ErrorCode.FORBIDDEN_OPERATION,
                "Max level " + building.getLevel() + " reached for dataId " + building.getDataId() + " on planet " + planetId + " and building " + building
            );
        }

        Construction construction = constructionFactory.create(building.getBuildingId(), constructionRequirements.getRequiredWorkPoints());
        resourceConsumptionService.processResourceRequirements(game.getGameId(), planet, LocationType.PLANET, construction.getConstructionId(), constructionRequirements.getRequiredResources());
        building.setConstruction(construction);

        gameDataProxy.saveItem(
            buildingToModelConverter.convert(building, game.getGameId()),
            constructionToModelConverter.convert(construction, game.getGameId())
        );

        return surfaceToResponseConverter.convert(surface);
    }
}

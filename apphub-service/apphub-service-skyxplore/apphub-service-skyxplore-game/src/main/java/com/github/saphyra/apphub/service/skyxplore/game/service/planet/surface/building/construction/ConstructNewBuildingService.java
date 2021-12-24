package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceBuildingResponse;
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
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.BuildingFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.consumption.ResourceConsumptionService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.BuildingToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.BuildingToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.ConstructionToModelConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConstructNewBuildingService {
    private final GameDao gameDao;
    private final AllBuildingService allBuildingService;
    private final BuildingFactory buildingFactory;
    private final ConstructionFactory constructionFactory;
    private final ResourceConsumptionService resourceConsumptionService;
    private final GameDataProxy gameDataProxy;
    private final BuildingToModelConverter buildingToModelConverter;
    private final ConstructionToModelConverter constructionToModelConverter;
    private final BuildingToResponseConverter buildingToResponseConverter;

    public SurfaceBuildingResponse constructNewBuilding(UUID userId, String dataId, UUID planetId, UUID surfaceId) {
        Optional<BuildingData> maybeBuildingData = allBuildingService.getOptional(dataId);
        if (maybeBuildingData.isEmpty()) {
            throw ExceptionFactory.invalidParam("dataId", "invalid value");
        }

        Game game = gameDao.findByUserIdValidated(userId);
        Planet planet = game
            .getUniverse()
            .findPlanetByIdAndOwnerValidated(userId, planetId);
        Surface surface = planet.getSurfaces()
            .findByIdValidated(surfaceId);

        if (nonNull(surface.getBuilding())) {
            throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS, "Building already exists on planet " + planetId + " and surface " + surfaceId);
        }

        BuildingData buildingData = maybeBuildingData.get();
        ConstructionRequirements constructionRequirements = buildingData.getConstructionRequirements()
            .get(1);

        if (!buildingData.getPlaceableSurfaceTypes().contains(surface.getSurfaceType())) {
            throw ExceptionFactory.notLoggedException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION, dataId + " cannot be built to surfaceType " + surface.getSurfaceType());
        }

        Building building = buildingFactory.create(dataId, surfaceId, 0);
        Construction construction = constructionFactory.create(building.getBuildingId(), constructionRequirements.getRequiredWorkPoints());
        building.setConstruction(construction);

        resourceConsumptionService.processResourceRequirements(game.getGameId(), planet, LocationType.PLANET, construction.getConstructionId(), constructionRequirements.getRequiredResources());

        surface.setBuilding(building);

        gameDataProxy.saveItem(
            buildingToModelConverter.convert(building, game.getGameId()),
            constructionToModelConverter.convert(construction, game.getGameId())
        );

        return buildingToResponseConverter.convert(building);
    }
}
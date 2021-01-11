package com.github.saphyra.apphub.service.skyxplore.game.service.planet;

import com.github.saphyra.apphub.api.skyxplore.game.server.SkyXploreGamePlanetController;
import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingsModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.CitizenResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetBuildingOverviewResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetPopulationOverviewResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetStorageResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.StorageSettingsResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.command.PriorityUpdateService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.command.RenameCitizenService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.query.PlanetBuildingOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.query.PlanetPopulationOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.query.PlanetStorageQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.query.PopulationQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting.StorageSettingCreationService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting.StorageSettingDeletionService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting.StorageSettingEditionService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting.StorageSettingsResponseQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.query.SurfaceQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PlanetControllerImpl implements SkyXploreGamePlanetController {
    private final GameDao gameDao;
    private final PlanetStorageQueryService planetStorageQueryService;
    private final PlanetPopulationOverviewQueryService planetPopulationOverviewQueryService;
    private final PlanetBuildingOverviewQueryService planetBuildingOverviewQueryService;
    private final StorageSettingsResponseQueryService storageSettingsResponseQueryService;
    private final SurfaceQueryService surfaceQueryService;
    private final StorageSettingCreationService storageSettingCreationService;
    private final StorageSettingDeletionService storageSettingDeletionService;
    private final StorageSettingEditionService storageSettingEditionService;
    private final PopulationQueryService populationQueryService;
    private final RenameCitizenService renameCitizenService;
    private final PriorityUpdateService priorityUpdateService;

    @Override
    //TODO unit test
    //TODO unt test
    //TODO api test
    public List<SurfaceResponse> getSurfaceOfPlanet(UUID planetId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to query the surface of planet {}", accessTokenHeader.getUserId(), planetId);
        return surfaceQueryService.getSurfaceOfPlanet(accessTokenHeader.getUserId(), planetId);
    }

    @Override
    //TODO unit test
    //TODO unt test
    //TODO api test
    public PlanetStorageResponse getStorageOfPlanet(UUID planetId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the storage of planet {}", accessTokenHeader.getUserId(), planetId);
        return planetStorageQueryService.getStorage(accessTokenHeader.getUserId(), planetId);
    }

    @Override
    //TODO unit test
    //TODO unt test
    //TODO api test
    public PlanetPopulationOverviewResponse getPopulationOverview(UUID planetId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the population overview of planet {}", accessTokenHeader.getUserId(), planetId);
        return planetPopulationOverviewQueryService.getPopulationOverview(accessTokenHeader.getUserId(), planetId);
    }

    @Override
    //TODO unit test
    //TODO unt test
    //TODO api test
    public Map<String, PlanetBuildingOverviewResponse> getBuildingOverview(UUID planetId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the building overview of planet {}", accessTokenHeader.getUserId(), planetId);
        return planetBuildingOverviewQueryService.getBuildingOverview(accessTokenHeader.getUserId(), planetId);
    }

    @Override
    //TODO unit test
    //TODO unt test
    //TODO api test
    public Map<String, Integer> getPriorities(UUID planetId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the priorities of planet {}", accessTokenHeader.getUserId(), planetId);

        return gameDao.findByUserIdValidated(accessTokenHeader.getUserId())
            .getUniverse()
            .findPlanetByIdValidated(planetId)
            .getPriorities()
            .entrySet()
            .stream()
            .collect(Collectors.toMap(entry -> entry.getKey().name().toLowerCase(), Map.Entry::getValue));
    }

    @Override
    //TODO unit test
    //TODO unt test
    //TODO api test
    public StorageSettingsResponse getStorageSettings(UUID planetId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the storageSettings of planet {}", accessTokenHeader.getUserId(), planetId);
        return storageSettingsResponseQueryService.getStorageSettings(accessTokenHeader.getUserId(), planetId);
    }

    @Override
    //TODO unit test
    //TODO unt test
    //TODO api test
    public void createStorageSetting(StorageSettingsModel request, UUID planetId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create storageSetting for resource {} on planet {}", accessTokenHeader.getUserId(), request.getDataId(), planetId);
        storageSettingCreationService.createStorageSetting(accessTokenHeader.getUserId(), planetId, request);
    }

    @Override
    //TODO unit test
    //TODO unt test
    //TODO api test
    public void deleteStorageSetting(UUID planetId, UUID storageSettingId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete storageSetting {} from planet {}", accessTokenHeader.getUserId(), storageSettingId, planetId);
        storageSettingDeletionService.deleteStorageSetting(accessTokenHeader.getUserId(), planetId, storageSettingId);
    }

    @Override
    //TODO unit test
    //TODO unt test
    //TODO api test
    public void editStorageSetting(StorageSettingsModel request, UUID planetId, UUID storageSettingId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit storageSetting {} on planet {}", accessTokenHeader.getUserId(), storageSettingId, planetId);
        storageSettingEditionService.edit(accessTokenHeader.getUserId(), planetId, storageSettingId, request);
    }

    @Override
    //TODO unit test
    //TODO unt test
    //TODO api test
    public List<CitizenResponse> getPopulation(UUID planetId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the population of planet {}", accessTokenHeader.getUserId(), planetId);
        return populationQueryService.getPopulation(accessTokenHeader.getUserId(), planetId);
    }

    @Override
    //TODO unit test
    //TODO unt test
    //TODO api test
    public void renameCitizen(OneParamRequest<String> newName, UUID planetId, UUID citizenId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to rename citizen {} on planet {}", accessTokenHeader.getUserId(), citizenId, planetId);
        renameCitizenService.renameCitizen(accessTokenHeader.getUserId(), planetId, citizenId, newName.getValue());
    }

    @Override
    //TODO unit test
    //TODO unt test
    //TODO api test
    public void updatePriority(OneParamRequest<Integer> newPriority, UUID planetId, String priorityType, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to update priority of {} on planet {}", accessTokenHeader.getUserId(), priorityType, planetId);
        priorityUpdateService.updatePriority(accessTokenHeader.getUserId(), planetId, priorityType, newPriority.getValue());
    }
}

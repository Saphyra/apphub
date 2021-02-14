package com.github.saphyra.apphub.api.skyxplore.model.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GameItemType {
    GAME(GameModel.class),
    PLAYER(PlayerModel.class),
    ALLIANCE(AllianceModel.class),
    UNIVERSE(UniverseModel.class),
    SOLAR_SYSTEM(SolarSystemModel.class),
    PLANET(PlanetModel.class),
    CITIZEN(CitizenModel.class),
    SKILL(SkillModel.class),
    SURFACE(SurfaceModel.class),
    BUILDING(BuildingModel.class),
    SYSTEM_CONNECTION(SystemConnectionModel.class),
    ALLOCATED_RESOURCE(AllocatedResourceModel.class),
    RESERVED_STORAGE(ReservedStorageModel.class), //TODO create saver
    STORED_RESOURCE(StoredResourceModel.class), //TODO create saver
    STORAGE_SETTING(StorageSettingModel.class), //TODO create saver
    PRIORITY(PriorityModel.class), //TODO create saver
    ;
    private final Class<? extends GameItem> modelType;
}

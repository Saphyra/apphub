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
    SKILL(SkillModel.class), //TODO create saver
    SURFACE(SurfaceModel.class), //TODO create saver
    BUILDING(BuildingModel.class), //TODO create saver
    SYSTEM_CONNECTION(SystemConnectionModel.class), //TODO create saver
    ALLOCATED_RESOURCE(AllocatedResourceModel.class), //TODO create saver
    RESERVED_STORAGE(ReservedStorageModel.class), //TODO create saver
    STORED_RESOURCE(StoredResourceModel.class), //TODO create saver
    STORAGE_SETTING(StorageSettingModel.class), //TODO create saver
    PRIORITY(PriorityModel.class), //TODO create saver
    ;
    private final Class<? extends GameItem> modelType;
}

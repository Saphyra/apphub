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
    CONSTRUCTION(ConstructionModel.class),
    SYSTEM_CONNECTION(SystemConnectionModel.class),
    ALLOCATED_RESOURCE(AllocatedResourceModel.class),
    RESERVED_STORAGE(ReservedStorageModel.class),
    STORED_RESOURCE(StoredResourceModel.class),
    STORAGE_SETTING(StorageSettingModel.class),
    PRIORITY(PriorityModel.class),
    COORDINATE(CoordinateModel.class),
    LINE(LineModel.class),
    PRODUCTION_ORDER(ProductionOrderModel.class),
    DURABILITY_ITEM_MODEL(DurabilityItemModel.class),
    PROCESS(ProcessModel.class);

    private final Class<? extends GameItem> modelType;
}

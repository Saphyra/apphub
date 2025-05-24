package com.github.saphyra.apphub.api.skyxplore.model.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GameItemType {
    GAME(GameModel.class),
    PLAYER(PlayerModel.class),
    ALLIANCE(AllianceModel.class),
    SOLAR_SYSTEM(SolarSystemModel.class),
    PLANET(PlanetModel.class),
    CITIZEN(CitizenModel.class),
    SKILL(SkillModel.class),
    SURFACE(SurfaceModel.class),
    BUILDING_MODULE(BuildingModuleModel.class),
    CONSTRUCTION(ConstructionModel.class),
    CONSTRUCTION_AREA(ConstructionAreaModel.class),
    DECONSTRUCTION(DeconstructionModel.class),
    RESERVED_STORAGE(ReservedStorageModel.class),
    STORED_RESOURCE(StoredResourceModel.class),
    STORAGE_SETTING(StorageSettingModel.class),
    PRIORITY(PriorityModel.class),
    COORDINATE(CoordinateModel.class),
    LINE(LineModel.class),
    PRODUCTION_ORDER(ProductionOrderModel.class), //TODO update to new schema
    DURABILITY(DurabilityModel.class),
    PROCESS(ProcessModel.class),
    CITIZEN_ALLOCATION(CitizenAllocationModel.class),
    BUILDING_MODULE_ALLOCATION(BuildingModuleAllocationModel.class),
    RESOURCE_DELIVERY_REQUEST(ResourceDeliveryRequestModel.class), //TODO save to database
    CONVOY(ResourceDeliveryRequestModel.class), //TODO save to database
    PRODUCTION_REQUEST(ProductionRequestModel.class), //TODO save to database
    ;

    private final Class<? extends GameItem> modelType;
}

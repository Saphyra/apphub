package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.building_module;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModuleModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BuildingModuleConverterTest {
    private static final UUID BUILDING_MODULE_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final String BUILDING_MODULE_ID_STRING = "building-module-id";
    private static final String GAME_ID_STRING = "game-id";
    private static final String CONSTRUCTION_AREA_ID_STRING = "construction-area-id";
    private static final String LOCATION_ID_STRING = "location";
    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private BuildingModuleConverter underTest;

    @Test
    void convertDomain() {
        BuildingModuleModel domain = new BuildingModuleModel();
        domain.setId(BUILDING_MODULE_ID);
        domain.setGameId(GAME_ID);
        domain.setConstructionAreaId(CONSTRUCTION_AREA_ID);
        domain.setLocation(LOCATION);
        domain.setDataId(DATA_ID);

        given(uuidConverter.convertDomain(BUILDING_MODULE_ID)).willReturn(BUILDING_MODULE_ID_STRING);
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(uuidConverter.convertDomain(CONSTRUCTION_AREA_ID)).willReturn(CONSTRUCTION_AREA_ID_STRING);
        given(uuidConverter.convertDomain(LOCATION)).willReturn(LOCATION_ID_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(BUILDING_MODULE_ID_STRING, BuildingModuleEntity::getBuildingModuleId)
            .returns(GAME_ID_STRING, BuildingModuleEntity::getGameId)
            .returns(CONSTRUCTION_AREA_ID_STRING, BuildingModuleEntity::getConstructionAreaId)
            .returns(LOCATION_ID_STRING, BuildingModuleEntity::getLocation)
            .returns(DATA_ID, BuildingModuleEntity::getDataId);
    }

    @Test
    void convertEntity() {
        BuildingModuleEntity entity = BuildingModuleEntity.builder()
            .buildingModuleId(BUILDING_MODULE_ID_STRING)
            .gameId(GAME_ID_STRING)
            .constructionAreaId(CONSTRUCTION_AREA_ID_STRING)
            .location(LOCATION_ID_STRING)
            .dataId(DATA_ID)
            .build();

        given(uuidConverter.convertEntity(BUILDING_MODULE_ID_STRING)).willReturn(BUILDING_MODULE_ID);
        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);
        given(uuidConverter.convertEntity(CONSTRUCTION_AREA_ID_STRING)).willReturn(CONSTRUCTION_AREA_ID);
        given(uuidConverter.convertEntity(LOCATION_ID_STRING)).willReturn(LOCATION);

        assertThat(underTest.convertEntity(entity))
            .returns(BUILDING_MODULE_ID, GameItem::getId)
            .returns(GAME_ID, BuildingModuleModel::getGameId)
            .returns(CONSTRUCTION_AREA_ID, BuildingModuleModel::getConstructionAreaId)
            .returns(LOCATION, BuildingModuleModel::getLocation)
            .returns(DATA_ID, BuildingModuleModel::getDataId)
            .returns(GameItemType.BUILDING_MODULE, GameItem::getType);
    }
}
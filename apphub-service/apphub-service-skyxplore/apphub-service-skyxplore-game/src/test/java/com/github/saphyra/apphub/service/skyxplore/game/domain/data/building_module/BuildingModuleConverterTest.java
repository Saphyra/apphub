package com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModuleModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.test.common.CustomAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BuildingModuleConverterTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID BUILDING_MODULE_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";

    @InjectMocks
    private BuildingModuleConverter underTest;

    @Mock
    private GameData gameData;

    @Mock
    private BuildingModules buildingModules;

    @Test
    void convert() {
        BuildingModule buildingModule = BuildingModule.builder()
            .buildingModuleId(BUILDING_MODULE_ID)
            .location(LOCATION)
            .constructionAreaId(CONSTRUCTION_AREA_ID)
            .dataId(DATA_ID)
            .build();

        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(buildingModules.stream()).willReturn(Stream.of(buildingModule));

        CustomAssertions.singleListAssertThat(underTest.convert(GAME_ID, gameData))
            .returns(BUILDING_MODULE_ID, GameItem::getId)
            .returns(GAME_ID, GameItem::getGameId)
            .returns(GameItemType.BUILDING_MODULE, GameItem::getType)
            .returns(LOCATION, BuildingModuleModel::getLocation)
            .returns(CONSTRUCTION_AREA_ID, BuildingModuleModel::getConstructionAreaId)
            .returns(DATA_ID, BuildingModuleModel::getDataId);
    }
}
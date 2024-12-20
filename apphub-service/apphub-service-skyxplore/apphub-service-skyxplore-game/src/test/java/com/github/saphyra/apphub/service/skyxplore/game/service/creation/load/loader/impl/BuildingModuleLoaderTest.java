package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModuleModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModules;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class BuildingModuleLoaderTest {
    private static final UUID BUILDING_MODULE_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";

    @Mock
    private GameItemLoader gameItemLoader;

    @InjectMocks
    private BuildingModuleLoader underTest;

    @Mock
    private GameData gameData;

    @Mock
    private BuildingModules buildingModules;

    @Mock
    private BuildingModule buildingModule;

    @Test
    void getGameItemType() {
        assertThat(underTest.getGameItemType()).isEqualTo(GameItemType.BUILDING_MODULE);
    }

    @Test
    void addToGameData() {
        given(gameData.getBuildingModules()).willReturn(buildingModules);

        underTest.addToGameData(gameData, List.of(buildingModule));

        then(buildingModules).should().addAll(List.of(buildingModule));
    }

    @Test
    void convert() {
        BuildingModuleModel model = new BuildingModuleModel();
        model.setId(BUILDING_MODULE_ID);
        model.setLocation(LOCATION);
        model.setConstructionAreaId(CONSTRUCTION_AREA_ID);
        model.setDataId(DATA_ID);

        assertThat(underTest.convert(model))
            .returns(BUILDING_MODULE_ID, BuildingModule::getBuildingModuleId)
            .returns(LOCATION, BuildingModule::getLocation)
            .returns(CONSTRUCTION_AREA_ID, BuildingModule::getConstructionAreaId)
            .returns(DATA_ID, BuildingModule::getDataId);
    }
}
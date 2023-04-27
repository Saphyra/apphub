package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Buildings;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BuildingLoaderTest {
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer LEVEL = 346;

    @Mock
    private GameItemLoader gameItemLoader;

    @InjectMocks
    private BuildingLoader underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Buildings buildings;

    @Mock
    private Building building;

    @Mock
    private BuildingModel model;

    @Test
    void getGameItemType() {
        assertThat(underTest.getGameItemType()).isEqualTo(GameItemType.BUILDING);
    }

    @Test
    void getArrayClass() {
        assertThat(underTest.getArrayClass()).isEqualTo(BuildingModel[].class);
    }

    @Test
    void addToGameData() {
        given(gameData.getBuildings()).willReturn(buildings);

        underTest.addToGameData(gameData, List.of(building));

        verify(buildings).addAll(List.of(building));
    }

    @Test
    void convert() {
        given(model.getId()).willReturn(BUILDING_ID);
        given(model.getLocation()).willReturn(LOCATION);
        given(model.getSurfaceId()).willReturn(SURFACE_ID);
        given(model.getDataId()).willReturn(DATA_ID);
        given(model.getLevel()).willReturn(LEVEL);

        Building result = underTest.convert(model);

        assertThat(result.getBuildingId()).isEqualTo(BUILDING_ID);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getSurfaceId()).isEqualTo(SURFACE_ID);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getLevel()).isEqualTo(LEVEL);
    }
}
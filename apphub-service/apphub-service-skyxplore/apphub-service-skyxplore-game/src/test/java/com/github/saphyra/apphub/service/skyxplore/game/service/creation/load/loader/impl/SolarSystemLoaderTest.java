package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SolarSystemModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystems;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SolarSystemLoaderTest {
    private static final UUID SOLAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final Integer RADIUS = 2435;
    private static final String DEFAULT_NAME = "default-name";
    private static final String CUSTOM_NAME = "custom-name";

    @Mock
    private GameItemLoader gameItemLoader;

    @InjectMocks
    private SolarSystemLoader underTest;

    @Mock
    private GameData gameData;

    @Mock
    private SolarSystems solarSystems;

    @Mock
    private SolarSystem solarSystem;

    @Mock
    private SolarSystemModel solarSystemModel;

    @Test
    void getGameItemType() {
        assertThat(underTest.getGameItemType()).isEqualTo(GameItemType.SOLAR_SYSTEM);
    }

    @Test
    void getArrayClass() {
        assertThat(underTest.getArrayClass()).isEqualTo(SolarSystemModel[].class);
    }

    @Test
    void addToGame() {
        given(gameData.getSolarSystems()).willReturn(solarSystems);

        underTest.addToGameData(gameData, List.of(solarSystem));

        verify(solarSystems).addAll(List.of(solarSystem));
    }

    @Test
    void convert() {
        given(solarSystemModel.getId()).willReturn(SOLAR_SYSTEM_ID);
        given(solarSystemModel.getRadius()).willReturn(RADIUS);
        given(solarSystemModel.getDefaultName()).willReturn(DEFAULT_NAME);
        given(solarSystemModel.getCustomNames()).willReturn(Map.of(USER_ID, CUSTOM_NAME));

        SolarSystem result = underTest.convert(solarSystemModel);

        assertThat(result.getSolarSystemId()).isEqualTo(SOLAR_SYSTEM_ID);
        assertThat(result.getRadius()).isEqualTo(RADIUS);
        assertThat(result.getDefaultName()).isEqualTo(DEFAULT_NAME);
        assertThat(result.getCustomNames()).containsEntry(USER_ID, CUSTOM_NAME);
    }
}
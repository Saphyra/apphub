package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.DurabilityModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.durability.Durabilities;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.durability.Durability;
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
class DurabilityLoaderTest {
    private static final UUID DURABILITY_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final Integer MAX_HIT_POINTS = 3456;
    private static final Integer CURRENT_HIT_POINTS = 34564;

    @Mock
    private GameItemLoader gameItemLoader;

    @InjectMocks
    private DurabilityLoader underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Durabilities durabilities;

    @Mock
    private Durability durability;

    @Mock
    private DurabilityModel model;

    @Test
    void getGameItemType() {
        assertThat(underTest.getGameItemType()).isEqualTo(GameItemType.DURABILITY);
    }

    @Test
    void getArrayClass() {
        assertThat(underTest.getArrayClass()).isEqualTo(DurabilityModel[].class);
    }

    @Test
    void addToGameData() {
        given(gameData.getDurabilities()).willReturn(durabilities);

        underTest.addToGameData(gameData, List.of(durability));

        verify(durabilities).addAll(List.of(durability));
    }

    @Test
    void convert() {
        given(model.getId()).willReturn(DURABILITY_ID);
        given(model.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(model.getMaxHitPoints()).willReturn(MAX_HIT_POINTS);
        given(model.getCurrentHitPoints()).willReturn(CURRENT_HIT_POINTS);

        Durability result = underTest.convert(model);

        assertThat(result.getDurabilityId()).isEqualTo(DURABILITY_ID);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.getMaxHitPoints()).isEqualTo(MAX_HIT_POINTS);
        assertThat(result.getCurrentHitPoints()).isEqualTo(CURRENT_HIT_POINTS);
    }
}
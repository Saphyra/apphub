package com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.impl;

import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenSatietyProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizens;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.TickTaskOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SatietyDecreaseTickTaskTest {
    private static final Integer SATIETY_DECREASED_PER_TICK = 25;

    @Mock
    private GameProperties gameProperties;

    @InjectMocks
    private SatietyDecreaseTickTask underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private SyncCache syncCache;

    @Mock
    private Citizen citizen;

    @Mock
    private CitizenProperties citizenProperties;

    @Mock
    private CitizenSatietyProperties satietyProperties;

    @Test
    void getOrder() {
        assertThat(underTest.getOrder()).isEqualTo(TickTaskOrder.SATIETY_DECREASE);
    }

    @Test
    void process() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getCitizens()).willReturn(CollectionUtils.toList(new Citizens(), citizen));
        given(gameProperties.getCitizen()).willReturn(citizenProperties);
        given(citizenProperties.getSatiety()).willReturn(satietyProperties);
        given(satietyProperties.getSatietyDecreasedPerTick()).willReturn(SATIETY_DECREASED_PER_TICK);

        underTest.process(game, syncCache);

        verify(citizen).decreaseSatiety(SATIETY_DECREASED_PER_TICK);
        verify(syncCache).citizenModified(citizen);
    }
}
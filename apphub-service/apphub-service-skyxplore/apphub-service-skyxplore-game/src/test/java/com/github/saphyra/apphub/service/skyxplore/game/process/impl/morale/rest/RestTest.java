package com.github.saphyra.apphub.service.skyxplore.game.process.impl.morale.rest;

import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.service.GameSleepService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RestTest {
    private static final int RESTORED_MORALE = 365;
    private static final int SLEEP_TIME = 26534;
    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private Game game;

    private Rest underTest;

    @Mock
    private GameSleepService gameSleepService;

    @BeforeEach
    void setUp() {
        underTest = new Rest(RESTORED_MORALE, SLEEP_TIME, game, applicationContextProxy);
    }

    @Test
    void call() throws Exception {
        given(applicationContextProxy.getBean(GameSleepService.class)).willReturn(gameSleepService);

        Rest result = underTest.call();

        verify(gameSleepService).sleep(game, SLEEP_TIME);

        assertThat(result).isEqualTo(underTest);
    }
}
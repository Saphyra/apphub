package com.github.saphyra.apphub.service.skyxplore.game.process.impl.morale.rest;

import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RestFactoryTest {
    private static final int RESTORED_MORALE = 234;
    private static final int SLEEP_TIME = 457;
    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @InjectMocks
    private RestFactory underTest;

    @Mock
    private Game game;

    @Test
    void create() {
        Rest result = underTest.create(RESTORED_MORALE, SLEEP_TIME, game);

        assertThat(result.getRestoredMorale()).isEqualTo(RESTORED_MORALE);
        assertThat(result.getSleepTime()).isEqualTo(SLEEP_TIME);
        assertThat(result.getGame()).isEqualTo(game);
        assertThat(result.getApplicationContextProxy()).isEqualTo(applicationContextProxy);
    }
}
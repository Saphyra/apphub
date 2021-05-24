package com.github.saphyra.apphub.service.skyxplore.lobby.service.active_friend;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ActiveUsersDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @InjectMocks
    private ActiveUsersDao underTest;

    @Test
    public void testFlow() {
        underTest.playerOnline(USER_ID);

        assertThat(underTest.isOnline(USER_ID)).isTrue();

        underTest.playerOnline(USER_ID);
        underTest.playerOffline(USER_ID);

        assertThat(underTest.isOnline(USER_ID)).isFalse();
    }
}
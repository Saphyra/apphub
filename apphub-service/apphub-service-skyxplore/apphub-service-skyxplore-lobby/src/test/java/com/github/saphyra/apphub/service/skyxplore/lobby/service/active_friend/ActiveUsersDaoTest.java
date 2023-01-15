package com.github.saphyra.apphub.service.skyxplore.lobby.service.active_friend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
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
package com.github.saphyra.apphub.service.skyxplore.lobby.controller;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SkyXploreLobbyEventControllerImplTest {
    private static final int EXPIRATION_MINUTES = 314;
    private static final LocalDateTime ACTUAL_DATE = LocalDateTime.now();

    @Mock
    private LobbyDao lobbyDao;

    @Mock
    private DateTimeUtil dateTimeUtil;

    private SkyXploreLobbyEventControllerImpl underTest;

    @Mock
    private Lobby lobby;

    @Before
    public void setUp() {
        underTest = SkyXploreLobbyEventControllerImpl.builder()
            .lobbyDao(lobbyDao)
            .dateTimeUtil(dateTimeUtil)
            .lobbyExpirationMinutes(EXPIRATION_MINUTES)
            .build();
    }

    @Test
    public void cleanupExpiredLobbies() {
        given(dateTimeUtil.getCurrentTime()).willReturn(ACTUAL_DATE);
        given(lobbyDao.getByLastAccessedBefore(ACTUAL_DATE.minusMinutes(EXPIRATION_MINUTES))).willReturn(Arrays.asList(lobby));

        underTest.cleanupExpiredLobbies();

        verify(lobbyDao).delete(lobby);
    }
}
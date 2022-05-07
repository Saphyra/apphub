package com.github.saphyra.apphub.service.skyxplore.lobby.controller.filter;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LobbyLastAccessInterceptorFilterTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final LocalDateTime ACTUAL_DATE = LocalDateTime.now();

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private LobbyDao lobbyDao;

    @InjectMocks
    private LobbyLastAccessInterceptorFilter underTest;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private Lobby lobby;

    @Test
    public void updateLastAccess() throws ServletException, IOException {
        given(accessTokenProvider.get()).willReturn(accessTokenHeader);
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(lobbyDao.findByUserId(USER_ID)).willReturn(Optional.of(lobby));
        given(dateTimeUtil.getCurrentTime()).willReturn(ACTUAL_DATE);

        underTest.doFilterInternal(request, response, filterChain);

        verify(lobby).setLastAccess(ACTUAL_DATE);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void lobbyNotFound() throws ServletException, IOException {
        given(accessTokenProvider.get()).willReturn(accessTokenHeader);
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(lobbyDao.findByUserId(USER_ID)).willReturn(Optional.empty());

        underTest.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void error() throws ServletException, IOException {
        given(accessTokenProvider.get()).willThrow(new RuntimeException());

        underTest.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}
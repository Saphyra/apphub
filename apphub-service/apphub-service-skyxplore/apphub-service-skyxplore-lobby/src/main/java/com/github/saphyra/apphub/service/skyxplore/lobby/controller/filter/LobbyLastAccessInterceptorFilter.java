package com.github.saphyra.apphub.service.skyxplore.lobby.controller.filter;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class LobbyLastAccessInterceptorFilter extends OncePerRequestFilter {
    private final AccessTokenProvider accessTokenProvider;
    private final DateTimeUtil dateTimeUtil;
    private final LobbyDao lobbyDao;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            AccessTokenHeader accessTokenHeader = accessTokenProvider.get();
            lobbyDao.findByUserId(accessTokenHeader.getUserId())
                .ifPresent(lobby -> lobby.setLastAccess(dateTimeUtil.getCurrentDateTime()));
        } catch (Exception e) {
            log.error("LobbyInterception failed.", e);
        }

        filterChain.doFilter(request, response);
    }
}

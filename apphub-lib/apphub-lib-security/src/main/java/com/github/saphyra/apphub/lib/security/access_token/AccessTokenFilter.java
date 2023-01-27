package com.github.saphyra.apphub.lib.security.access_token;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_util.converter.AccessTokenHeaderConverter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccessTokenFilter extends OncePerRequestFilter {
    private final AccessTokenProvider accessTokenProvider;
    private final AccessTokenHeaderConverter accessTokenHeaderConverter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            getHeader(request).ifPresent(accessTokenProvider::set);
            filterChain.doFilter(request, response);
        } finally {
            accessTokenProvider.clear();
        }
    }

    private Optional<AccessTokenHeader> getHeader(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(Constants.ACCESS_TOKEN_HEADER))
            .filter(maybeAccessToken -> !isBlank(maybeAccessToken))
            .map(accessTokenHeaderConverter::convert);
    }
}

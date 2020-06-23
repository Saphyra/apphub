package com.github.saphyra.apphub.lib.security.access_token;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static org.apache.commons.lang.StringUtils.isBlank;

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

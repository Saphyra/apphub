package com.github.saphyra.apphub.lib.request_validation.locale;

import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.LocaleProvider;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.config.common.CommonConfigProperties;
import com.github.saphyra.apphub.lib.error_handler.service.ErrorResponseFactory;
import com.github.saphyra.apphub.lib.error_handler.service.ErrorResponseWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
class LocaleMandatoryFilter extends OncePerRequestFilter {
    private final AntPathMatcher antPathMatcher;
    private final CommonConfigProperties commonConfigProperties;
    private final ErrorResponseFactory errorResponseFactory;
    private final LocaleProvider localeProvider;
    private final ObjectMapperWrapper objectMapperWrapper;
    private final LocaleMandatoryFilterConfiguration localeMandatoryFilterConfiguration;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<String> locale = localeProvider.getLocale(request);
        if (locale.isPresent() || isWhiteListedEndpoint(request)) {
            filterChain.doFilter(request, response);
        } else {
            log.warn("Locale header is not present in the request {} - {}", request.getMethod(), request.getRequestURI());
            ErrorResponseWrapper errorResponse = errorResponseFactory.create(
                commonConfigProperties.getDefaultLocale(),
                HttpStatus.BAD_REQUEST,
                ErrorCode.LOCALE_NOT_FOUND.name()
            );

            response.setStatus(errorResponse.getStatus().value());
            response.setCharacterEncoding("UTF-8");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            PrintWriter writer = response.getWriter();
            writer.write(objectMapperWrapper.writeValueAsString(errorResponse.getErrorResponse()));
            writer.flush();
            writer.close();
        }
    }

    private boolean isWhiteListedEndpoint(HttpServletRequest request) {
        String method = request.getMethod();
        String requestUri = request.getRequestURI();

        return localeMandatoryFilterConfiguration.getWhiteListedEndpoints()
            .stream()
            .anyMatch(whiteListedEndpoint -> antPathMatcher.match(whiteListedEndpoint.getPath(), requestUri) && whiteListedEndpoint.getMethod().equals(method));
    }
}

package com.github.saphyra.apphub.service.platform.main_gateway.filters;

import com.github.saphyra.apphub.lib.config.common.endpoints.UserEndpoints;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.platform.main_gateway.config.FilterOrder;
import com.github.saphyra.apphub.service.platform.main_gateway.service.locale.UserSettingLocaleResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ChangeLocaleFilter implements GlobalFilter, Ordered {
    private final ErrorReporterService errorReporterService;
    private final AntPathMatcher antPathMatcher;
    private final UserSettingLocaleResolver userSettingLocaleResolver;

    public ChangeLocaleFilter(@Lazy ErrorReporterService errorReporterService, AntPathMatcher antPathMatcher, @Lazy UserSettingLocaleResolver userSettingLocaleResolver) {
        this.errorReporterService = errorReporterService;
        this.antPathMatcher = antPathMatcher;
        this.userSettingLocaleResolver = userSettingLocaleResolver;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Mono<Void> result = chain.filter(exchange);
        try {
            ServerHttpRequest request = exchange.getRequest();
            String uri = request
                .getURI()
                .getPath();
            if (antPathMatcher.match(UserEndpoints.ACCOUNT_CHANGE_LANGUAGE, uri)) {
                userSettingLocaleResolver.invalidate(request.getCookies());
            }
        } catch (Exception e) {
            log.error("ChangeLocaleFilter process failed.");
            errorReporterService.report("ChangeLocaleFilter processing failed.", e);
        }
        return result;
    }

    @Override
    public int getOrder() {
        return FilterOrder.CHANGE_LOCALE_FILTER.getOrder();
    }
}

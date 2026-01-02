package com.github.saphyra.apphub.service.platform.main_gateway.filters;

import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.service.platform.main_gateway.config.DelayProperties;
import com.github.saphyra.apphub.service.platform.main_gateway.config.DelayedPath;
import com.github.saphyra.apphub.service.platform.main_gateway.config.FilterOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

@Component
@ConditionalOnProperty(prefix = "delay.filter", value = "enabled", havingValue = "true")
@Slf4j
@RequiredArgsConstructor
class DelayFilter implements GlobalFilter, Ordered {
    private final AntPathMatcher antPathMatcher;
    private final DelayProperties delayProperties;
    private final Random random;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Optional<DelayedPath> maybePath = getMatchedPath(exchange.getRequest());

        if (maybePath.isPresent()) {
            DelayedPath delayedPath = maybePath.get();

            long delayMillis = random.randLong(delayedPath.getMinDelay().toMillis(), delayedPath.getMaxDelay().toMillis());

            log.info("{} - {} is delayed for {} ms", exchange.getRequest().getMethod(), exchange.getRequest().getURI(), delayMillis);

            return Mono.just("a")
                .then(Mono.delay(Duration.ofMillis(delayMillis)))
                .flatMap(_ -> chain.filter(exchange));
        }

        return chain.filter(exchange);
    }

    private Optional<DelayedPath> getMatchedPath(ServerHttpRequest request) {
        return delayProperties.getPaths()
            .stream()
            .filter(DelayedPath::isEnabled)
            .filter(delayedPath -> antPathMatcher.match(delayedPath.getPath(), request.getURI().getPath()))
            .findFirst();
    }

    @Override
    public int getOrder() {
        return FilterOrder.DELAY_FILTER.getOrder();
    }
}

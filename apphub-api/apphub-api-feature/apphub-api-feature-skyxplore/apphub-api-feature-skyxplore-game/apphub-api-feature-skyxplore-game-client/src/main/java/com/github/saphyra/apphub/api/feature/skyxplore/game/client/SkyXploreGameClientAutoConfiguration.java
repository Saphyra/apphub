package com.github.saphyra.apphub.api.feature.skyxplore.game.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@AutoConfiguration
@Slf4j
public class SkyXploreGameClientAutoConfiguration {
    private final String baseUrl;

    public SkyXploreGameClientAutoConfiguration(@Value("${baseUrls.skyxploreGame}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Bean
    @ConditionalOnMissingBean
    public SkyXploreGameCreationApiClient skyXploreGameCreationApiClient(RestClient.Builder clientBuilder) {
        log.info("Creating SkyXploreGameCreationApiClient with base URL: {}", baseUrl);

        RestClient restClient = clientBuilder.baseUrl(baseUrl)
            .build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);

        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(SkyXploreGameCreationApiClient.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public SkyXploreGameApiClient skyXploreGameApiClient(RestClient.Builder clientBuilder) {
        log.info("Creating SkyXploreGameApiClient with base URL: {}", baseUrl);

        RestClient restClient = clientBuilder.baseUrl(baseUrl)
            .build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);

        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(SkyXploreGameApiClient.class);
    }
}



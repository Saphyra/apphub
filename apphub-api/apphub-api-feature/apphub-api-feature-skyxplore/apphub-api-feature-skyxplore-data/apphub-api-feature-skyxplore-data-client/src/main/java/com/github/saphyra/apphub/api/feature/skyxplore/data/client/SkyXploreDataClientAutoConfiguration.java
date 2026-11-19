package com.github.saphyra.apphub.api.feature.skyxplore.data.client;

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
public class SkyXploreDataClientAutoConfiguration {
    private final String baseUrl;

    public SkyXploreDataClientAutoConfiguration(@Value("${baseUrls.skyxploreData}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Bean
    @ConditionalOnMissingBean
    public SkyXploreSavedGameClient skyXploreSavedGameClient(RestClient.Builder clientBuilder) {
        log.info("Creating SkyXploreSavedGameClient with base URL: {}", baseUrl);

        RestClient restClient = clientBuilder.baseUrl(baseUrl)
            .build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);

        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(SkyXploreSavedGameClient.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public SkyXploreDataFriendApiClient skyXploreDataFriendApiClient(RestClient.Builder clientBuilder) {
        log.info("Creating SkyXploreDataFriendApiClient with base URL: {}", baseUrl);

        RestClient restClient = clientBuilder.baseUrl(baseUrl)
            .build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);

        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(SkyXploreDataFriendApiClient.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public SkyXploreCharacterDataApiClient skyXploreCharacterDataApiClient(RestClient.Builder clientBuilder) {
        log.info("Creating SkyXploreCharacterDataApiClient with base URL: {}", baseUrl);

        RestClient restClient = clientBuilder.baseUrl(baseUrl)
            .build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);

        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(SkyXploreCharacterDataApiClient.class);
    }
}



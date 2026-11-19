package com.github.saphyra.apphub.api.platform.storage.client;

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
public class StorageClientAutoConfiguration {
    private final String baseUrl;

    public StorageClientAutoConfiguration(@Value("${baseUrls.storage}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Bean
    @ConditionalOnMissingBean
    public StorageClient storageClient(RestClient.Builder clientBuilder) {
        log.info("Creating StorageClient with base URL: {}", baseUrl);

        RestClient restClient = clientBuilder.baseUrl(baseUrl)
            .build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);

        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(StorageClient.class);
    }
}


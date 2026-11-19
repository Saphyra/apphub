package com.github.saphyra.apphub.api.etc.user.client;

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
public class UserClientAutoConfiguration {
    private final String baseUrl;

    public UserClientAutoConfiguration(@Value("${baseUrls.user}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Bean
    @ConditionalOnMissingBean
    public AccountClient accountClient(RestClient.Builder clientBuilder) {
        log.info("Creating AccountClient with base URL: {}", baseUrl);

        RestClient restClient = clientBuilder.baseUrl(baseUrl)
            .build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);

        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(AccountClient.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthorizationClient authorizationClient(RestClient.Builder clientBuilder) {
        log.info("Creating AuthorizationClient with base URL: {}", baseUrl);

        RestClient restClient = clientBuilder.baseUrl(baseUrl)
            .build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);

        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(AuthorizationClient.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public DisabledRoleClient disabledRoleClient(RestClient.Builder clientBuilder) {
        log.info("Creating DisabledRoleClient with base URL: {}", baseUrl);

        RestClient restClient = clientBuilder.baseUrl(baseUrl)
            .build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);

        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(DisabledRoleClient.class);
    }
}

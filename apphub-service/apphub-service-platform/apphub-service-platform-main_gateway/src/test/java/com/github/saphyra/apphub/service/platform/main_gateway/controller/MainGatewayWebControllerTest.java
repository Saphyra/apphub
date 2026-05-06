package com.github.saphyra.apphub.service.platform.main_gateway.controller;

import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.service.platform.main_gateway.service.InvalidatedAccessTokenService;
import com.github.saphyra.apphub.service.platform.main_gateway.service.authorization.TokenParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class MainGatewayWebControllerTest {
    private static final String ACCESS_TOKEN_STRING = "access-token-string";
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();

    @Mock
    private TokenParser tokenParser;

    @Mock
    private InvalidatedAccessTokenService invalidatedAccessTokenService;

    @InjectMocks
    private MainGatewayWebController underTest;

    @Test
    void invalidateAccessToken() {
        AccessToken accessToken = AccessToken.builder()
            .accessTokenId(ACCESS_TOKEN_ID)
            .build();
        given(tokenParser.verifyAccessToken(ACCESS_TOKEN_STRING)).willReturn(Mono.just(accessToken));

        ResponseEntity<Void> result = underTest.invalidateAccessToken(ACCESS_TOKEN_STRING).block();

        then(invalidatedAccessTokenService).should().add(ACCESS_TOKEN_ID);
        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.TEMPORARY_REDIRECT);
        assertThat(result.getHeaders().getLocation()).isEqualTo(URI.create("/web"));
    }
}
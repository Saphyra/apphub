package com.github.saphyra.apphub.service.platform.main_gateway.service;

import com.github.saphyra.apphub.service.platform.main_gateway.config.AuthorizationProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class InvalidatedRefreshTokenServiceTest {
    private static final UUID REFRESH_TOKEN_ID = UUID.randomUUID();

    @Mock
    private AuthorizationProperties authorizationProperties;

    private InvalidatedRefreshTokenService underTest;

    @BeforeEach
    void setUp() {
        given(authorizationProperties.getAccessTokenExpiration()).willReturn(Duration.ofSeconds(3));

        underTest = new InvalidatedRefreshTokenService(authorizationProperties);
    }

    @Test
    void testFlow() throws InterruptedException {
        underTest.add(REFRESH_TOKEN_ID);

        assertThat(underTest.contains(REFRESH_TOKEN_ID)).isTrue();

        Thread.sleep(3000);

        assertThat(underTest.contains(REFRESH_TOKEN_ID)).isFalse();
    }
}
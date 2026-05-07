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
class InvalidatedAccessTokenServiceTest {
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();

    @Mock
    private AuthorizationProperties authorizationProperties;

    private InvalidatedAccessTokenService underTest;

    @BeforeEach
    void setUp() {
        given(authorizationProperties.getAccessTokenExpiration()).willReturn(Duration.ofSeconds(3));

        underTest = new InvalidatedAccessTokenService(authorizationProperties);
    }

    @Test
    void testFlow() throws InterruptedException {
        underTest.add(ACCESS_TOKEN_ID);

        assertThat(underTest.contains(ACCESS_TOKEN_ID)).isTrue();

        Thread.sleep(3000);

        assertThat(underTest.contains(ACCESS_TOKEN_ID)).isFalse();
    }

}
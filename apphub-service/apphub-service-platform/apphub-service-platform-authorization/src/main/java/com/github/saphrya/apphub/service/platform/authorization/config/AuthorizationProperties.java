package com.github.saphrya.apphub.service.platform.authorization.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Data
@Configuration
public class AuthorizationProperties {
    @Value("${authorization.issuer}")
    private String issuer;

    @Value("${authorization.expirationDuration.refreshToken.rememberMe}")
    private Duration refreshTokenExpirationRememberMe;

    @Value("${authorization.expirationDuration.refreshToken.default}")
    private Duration refreshTokenExpirationDefault;

    @Value("${authorization.expirationDuration.accessToken}")
    private Duration accessTokenExpiration;
}

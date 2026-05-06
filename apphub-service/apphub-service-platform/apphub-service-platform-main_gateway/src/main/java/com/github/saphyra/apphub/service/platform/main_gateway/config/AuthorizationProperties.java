package com.github.saphyra.apphub.service.platform.main_gateway.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@Data
public class AuthorizationProperties {
    @Value("${authorization.issuer}")
    private String issuer;

    @Value("${authorization.accessTokenExpiration}")
    private Duration accessTokenExpiration;
}

package com.github.saphrya.apphub.service.platform.authorization.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Data
@Configuration
@ToString(exclude = "dynamoDbSecretKey")
@Slf4j
public class AuthorizationProperties {
    @Value("${authorization.issuer}")
    private String issuer;

    @Value("${authorization.expirationDuration.refreshToken.rememberMe}")
    private Duration refreshTokenExpirationRememberMe;

    @Value("${authorization.expirationDuration.refreshToken.default}")
    private Duration refreshTokenExpirationDefault;

    @Value("${authorization.expirationDuration.accessToken}")
    private Duration accessTokenExpiration;

    @Value("${aws.dynamoDb.accessKeyId}")
    private String dynamoDbAccessKeyId;

    @Value("${aws.dynamoDb.secretKey}")
    private String dynamoDbSecretKey;

    @Value("${aws.dynamoDb.url}")
    private String dynamoDbUrl;

    @Value("${aws.dynamoDb.refreshToken.tableName}")
    private String refreshTokenTableName;

    @PostConstruct
    void logSelf() {
        log.info("{}", this);
    }
}

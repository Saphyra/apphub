package com.github.saphyra.apphub.service.platform.main_gateway.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class AuthorizationProperties {
    @Value("${authorization.issuer}")
    private String issuer;
}

package com.github.saphyra.apphub.service.user.authentication;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class AuthenticationProperties {
    @Value("${accessToken.cookie.expirationDays}")
    private Integer accessTokenCookieExpirationDays;

    @Value("${accessToken.expirationMinutes}")
    private Integer accessTokenExpirationMinutes;
}

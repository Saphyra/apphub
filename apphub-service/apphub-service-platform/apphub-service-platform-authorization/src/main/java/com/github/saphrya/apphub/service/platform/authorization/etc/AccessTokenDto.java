package com.github.saphrya.apphub.service.platform.authorization.etc;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AccessTokenDto {
    private final String jwt;
    private final LocalDateTime expiration;
}

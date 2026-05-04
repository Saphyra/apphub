package com.github.saphyra.apphub.integration.structure.api.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Deprecated(forRemoval = true) //TODO remove
public class LoginResponse {
    private String accessToken;
    private Integer expirationDays;
}

package com.hithub.saphyra.apphub.api.user.authentication.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LoginResponse {
    private UUID accessTokenId;
    private Integer expirationDays;
}

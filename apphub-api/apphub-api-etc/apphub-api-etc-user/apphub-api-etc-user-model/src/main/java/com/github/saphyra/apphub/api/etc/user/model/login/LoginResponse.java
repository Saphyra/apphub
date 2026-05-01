package com.github.saphyra.apphub.api.etc.user.model.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Deprecated(forRemoval = true) //TODO delete
public class LoginResponse {
    private UUID accessTokenId;
    private Integer expirationDays;
}

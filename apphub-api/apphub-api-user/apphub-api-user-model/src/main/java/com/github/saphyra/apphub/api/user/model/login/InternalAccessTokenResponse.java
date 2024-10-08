package com.github.saphyra.apphub.api.user.model.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternalAccessTokenResponse {
    private UUID accessTokenId;
    private UUID userId;
    private List<String> roles;
}

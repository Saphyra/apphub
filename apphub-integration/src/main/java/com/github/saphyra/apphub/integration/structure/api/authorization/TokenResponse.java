package com.github.saphyra.apphub.integration.structure.api.authorization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenResponse {
    private Token accessToken;
    private Token refreshToken;
}

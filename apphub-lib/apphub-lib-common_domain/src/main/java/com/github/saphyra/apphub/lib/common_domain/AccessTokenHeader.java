package com.github.saphyra.apphub.lib.common_domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessTokenHeader {
    private UUID accessTokenId;
    private UUID userId;
}

package com.github.saphyra.apphub.lib.common_domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AccessTokenHeader {
    private UUID accessTokenId;

    private UUID userId;

    @Builder.Default
    private List<String> roles = new ArrayList<>();
}

package com.github.saphyra.apphub.lib.common_domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessTokenHeader {
    private UUID accessTokenId;

    @NonNull
    private UUID userId;

    @NonNull
    @Builder.Default
    private List<String> roles = new ArrayList<>();
}

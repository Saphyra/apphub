package com.github.saphyra.apphub.integration.structure.api.authorization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Token {
    private String jwt;
    private String path;
    private Long expiration;
}

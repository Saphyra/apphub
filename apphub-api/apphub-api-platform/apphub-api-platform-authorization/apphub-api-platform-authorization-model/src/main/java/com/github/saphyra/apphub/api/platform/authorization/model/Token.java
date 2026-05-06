package com.github.saphyra.apphub.api.platform.authorization.model;

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

package com.github.saphyra.apphub.integration.structure.community;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SearchResultItem {
    private UUID userId;
    private String email;
    private String username;
}

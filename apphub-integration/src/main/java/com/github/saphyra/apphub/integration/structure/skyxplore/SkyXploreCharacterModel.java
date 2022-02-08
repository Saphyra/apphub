package com.github.saphyra.apphub.integration.structure.skyxplore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SkyXploreCharacterModel {
    private UUID id;
    private String name;

    public static SkyXploreCharacterModel valid() {
        return valid("skyxplore-character-");
    }

    public static SkyXploreCharacterModel valid(String identifier) {
        return SkyXploreCharacterModel.builder()
            .name((identifier + UUID.randomUUID()).substring(0, 29))
            .build();
    }
}

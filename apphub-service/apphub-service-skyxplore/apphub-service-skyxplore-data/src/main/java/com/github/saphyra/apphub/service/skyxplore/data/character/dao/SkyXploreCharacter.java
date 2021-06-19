package com.github.saphyra.apphub.service.skyxplore.data.character.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
public class SkyXploreCharacter {
    private final UUID userId;
    private String name;
}

package com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet;

import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import static java.util.Objects.isNull;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Slf4j
//TODO unit test
public class Planet {
    private final UUID planetId;
    private final UUID solarSystemId;
    private final String defaultName;
    @Builder.Default
    private final OptionalMap<UUID, String> customNames = new OptionalHashMap<>();
    private final int size;
    private final double orbitRadius;
    private final double orbitSpeed;
    private UUID owner;

    public boolean hasOwner() {
        return !isNull(owner);
    }
}

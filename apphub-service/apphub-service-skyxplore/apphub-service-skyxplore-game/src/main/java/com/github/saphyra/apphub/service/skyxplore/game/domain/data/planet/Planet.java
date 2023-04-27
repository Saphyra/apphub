package com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet;

import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import static java.util.Objects.isNull;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Slf4j
public class Planet {
    @NonNull
    private final UUID planetId;

    @NonNull
    private final UUID solarSystemId;

    @NonNull
    private final String defaultName;
    @Builder.Default
    private final OptionalMap<UUID, String> customNames = new OptionalHashMap<>();

    @NonNull
    private final Integer size;

    @NonNull
    private final Double orbitRadius;

    @NonNull
    private final Double orbitSpeed;

    private UUID owner;

    public boolean hasOwner() {
        return !isNull(owner);
    }
}

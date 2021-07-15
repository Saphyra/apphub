package com.github.saphyra.apphub.service.skyxplore.game.domain.map;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class SolarSystem {
    private final UUID solarSystemId;
    private final int radius;
    private final String defaultName;

    @Builder.Default
    private final OptionalMap<UUID, String> customNames = new OptionalHashMap<>();
    private final CoordinateModel coordinate;
    private final Map<UUID, Planet> planets;
}

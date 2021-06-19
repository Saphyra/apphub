package com.github.saphyra.apphub.api.skyxplore.model.game;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PlanetModel extends GameItem {
    private UUID solarSystemId;
    private String defaultName;
    private Map<UUID, String> customNames;
    private Coordinate coordinate;
    private Integer size;
    private UUID owner;
}

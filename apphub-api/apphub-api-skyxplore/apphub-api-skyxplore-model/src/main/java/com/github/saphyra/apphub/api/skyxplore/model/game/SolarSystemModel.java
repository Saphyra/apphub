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
public class SolarSystemModel extends GameItem {
    private int radius;
    private String defaultName;
    private Map<UUID, String> customNames;
    private Coordinate coordinate;
}

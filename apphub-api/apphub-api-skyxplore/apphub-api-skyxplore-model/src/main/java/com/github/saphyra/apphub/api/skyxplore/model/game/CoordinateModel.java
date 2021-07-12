package com.github.saphyra.apphub.api.skyxplore.model.game;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CoordinateModel extends GameItem {
    private UUID referenceId;
    private Coordinate coordinate;

    public Double getX() {
        return coordinate.getX();
    }

    public Double getY() {
        return coordinate.getY();
    }
}

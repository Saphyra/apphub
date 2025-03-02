package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static java.util.Objects.isNull;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class StarSystemPosition {
    private Double x;
    private Double y;
    private Double z;

    public static StarSystemPosition parse(Double[] starPosition) {
        if (isNull(starPosition) || starPosition.length != 3) {
            return null;
        }

        return new StarSystemPosition(starPosition[0], starPosition[1], starPosition[2]);
    }
}

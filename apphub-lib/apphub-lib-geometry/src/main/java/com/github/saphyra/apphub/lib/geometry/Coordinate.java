package com.github.saphyra.apphub.lib.geometry;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
public class Coordinate {
    private final BigDecimal x;
    private final BigDecimal y;
}

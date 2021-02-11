package com.github.saphyra.apphub.api.skyxplore.model.game;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class CitizenModel extends GameItem {
    private UUID location;
    private String locationType;
    private String name;
    private int morale;
    private int satiety;
}

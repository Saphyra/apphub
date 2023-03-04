package com.github.saphyra.apphub.service.skyxplore.game.config.properties;

import lombok.Data;

@Data
public class CitizenMoraleProperties {
    private int max;
    private int workEfficiencyDropUnder;
    private double minEfficiency;
    private double moralePerWorkPoints;
    private int regenPerSecond;
    private int minRestSeconds;
    private int maxRestSeconds;
}

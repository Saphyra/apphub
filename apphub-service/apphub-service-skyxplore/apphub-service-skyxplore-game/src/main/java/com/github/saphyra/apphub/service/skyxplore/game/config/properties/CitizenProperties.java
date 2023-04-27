package com.github.saphyra.apphub.service.skyxplore.game.config.properties;

import lombok.Data;

@Data
public class CitizenProperties {
    private int workPointsPerTick;
    private int maxWorkPointsBatch;
    private CitizenMoraleProperties morale;
    private CitizenSkillProperties skill;
    private CitizenSatietyProperties satiety;
    private CitizenHitPointsProperties hitPoints;
}

package com.github.saphyra.apphub.service.skyxplore.game.config.properties;

import lombok.Data;

@Data
public class CitizenProperties {
    private int workPointsPerSeconds;
    private CitizenMoraleProperties morale;
    private CitizenSkillProperties skill;
    private CitizenSatietyProperties satiety;
    private CitizenHitPointsProperties hitPoints;
}

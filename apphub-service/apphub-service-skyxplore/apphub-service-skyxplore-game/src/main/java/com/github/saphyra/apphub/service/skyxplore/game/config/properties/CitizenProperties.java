package com.github.saphyra.apphub.service.skyxplore.game.config.properties;

import com.github.saphyra.apphub.api.skyxplore.response.game.citizen.CitizenStat;
import lombok.Data;

import java.util.Map;

@Data
public class CitizenProperties {
    private int workPointsPerTick;
    private int maxWorkPointsBatch;
    private CitizenMoraleProperties morale;
    private CitizenSkillProperties skill;
    private CitizenSatietyProperties satiety;
    private CitizenHitPointsProperties hitPoints;
    private Map<CitizenStat, Integer> maxStatValues;
}

package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString(exclude = {"skills", "soldierData"})
public class Citizen {
    private final UUID citizenId;
    private UUID location;
    private LocationType locationType;
    private String name;
    private int morale;
    private volatile int satiety;
    private final Map<SkillType, Skill> skills;
    private final SoldierData soldierData;

    public void reduceMorale(int workPointsUsed) {
        morale -= workPointsUsed;
    }
}

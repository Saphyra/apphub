package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Skill {
    private final UUID skillId;
    private final UUID citizenId;
    private final SkillType skillType;
    private int level;
    private int experience;
    private int nextLevel;
}
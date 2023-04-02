package com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill;

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
    private Integer level;
    private Integer experience;
    private Integer nextLevel;

    public void increaseExperience(int experience) {
        this.experience += experience;
    }

    public void increaseLevel() {
        this.level += 1;
    }
}

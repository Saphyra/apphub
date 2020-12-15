package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Skill {
    private SkillType skillType;
    private int level;
    private int experience;
    private int nextLevel;
}

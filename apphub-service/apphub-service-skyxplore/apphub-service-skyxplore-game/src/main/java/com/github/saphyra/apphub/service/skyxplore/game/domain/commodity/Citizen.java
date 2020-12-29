package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Citizen {
    private final UUID citizenId;
    private String name;
    private Integer morale;
    private Integer satiety;
    private final Map<SkillType, Skill> skills;
}

package com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;

import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.stream.Collectors;

//TODO unit test
public class Skills extends Vector<Skill> {
    public Skill findByCitizenIdAndSkillType(UUID citizenId, SkillType skillType) {
        return stream()
            .filter(skill -> skill.getCitizenId().equals(citizenId))
            .filter(skill -> skill.getSkillType() == skillType)
            .findFirst()
            .orElseThrow();
    }

    public List<Skill> getByCitizenId(UUID citizenId) {
        return stream()
            .filter(skill -> skill.getCitizenId().equals(citizenId))
            .collect(Collectors.toList());
    }
}

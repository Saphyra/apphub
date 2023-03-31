package com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.stream.Collectors;

public class Skills extends Vector<Skill> {
    public Skill findByCitizenIdAndSkillType(UUID citizenId, SkillType skillType) {
        return stream()
            .filter(skill -> skill.getCitizenId().equals(citizenId))
            .filter(skill -> skill.getSkillType() == skillType)
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.loggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Skill not found by citizenId " + citizenId + " and skillType " + skillType));
    }

    public List<Skill> getByCitizenId(UUID citizenId) {
        return stream()
            .filter(skill -> skill.getCitizenId().equals(citizenId))
            .collect(Collectors.toList());
    }
}

package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.education;

import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class EducationValidator {
    public void validate(List<Education> educations) {
        ValidationUtil.notNull(educations, "educations");

        educations.forEach(this::validate);
    }

    private void validate(Education education) {
        ValidationUtil.notBlank(education.getId(), "education.id");
        ValidationUtil.notNull(education.getSkill(), "education.skill");

        Range<Integer> levelLimit = education.getLevelLimit();
        ValidationUtil.notNull(levelLimit, "education.levelLimit");
        ValidationUtil.atLeast(levelLimit.getMin(), 0, "education.levelLimit.min");
        ValidationUtil.atLeast(levelLimit.getMax(), levelLimit.getMin() + 1, "education.levelLimit.max");
    }
}

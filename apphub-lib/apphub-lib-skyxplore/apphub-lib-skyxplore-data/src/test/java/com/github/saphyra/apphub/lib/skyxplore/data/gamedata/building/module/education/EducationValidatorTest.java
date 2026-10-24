package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.education;

import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EducationValidatorTest {
    @InjectMocks
    private EducationValidator underTest;

    @Mock
    private Education education;

    @Test
    void nullEducations() {
        ExceptionValidator.validateInvalidParam(() -> underTest.validate(null), "educations", ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void blankId() {
        given(education.getId()).willReturn(" ");

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(List.of(education)), "education.id", ValidationUtil.MUST_NOT_BE_NULL_OR_BLANK);
    }

    @Test
    void nullSkill() {
        given(education.getId()).willReturn("id");

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(List.of(education)), "education.skill", ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void nullLevelLimit() {
        given(education.getId()).willReturn("id");
        given(education.getSkill()).willReturn(SkillType.AIMING);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(List.of(education)), "education.levelLimit", ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void minLevelTooLow() {
        given(education.getId()).willReturn("id");
        given(education.getSkill()).willReturn(SkillType.AIMING);
        given(education.getLevelLimit()).willReturn(new Range<>(-1, 1));

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(List.of(education)), "education.levelLimit.min", ValidationUtil.TOO_LOW);
    }

    @Test
    void maxLevelTooLow() {
        given(education.getId()).willReturn("id");
        given(education.getSkill()).willReturn(SkillType.AIMING);
        given(education.getLevelLimit()).willReturn(new Range<>(0, 0));

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(List.of(education)), "education.levelLimit.max", ValidationUtil.TOO_LOW);
    }

    @Test
    void valid() {
        given(education.getId()).willReturn("id");
        given(education.getSkill()).willReturn(SkillType.AIMING);
        given(education.getLevelLimit()).willReturn(new Range<>(0, 1));

        underTest.validate(List.of(education));
    }
}
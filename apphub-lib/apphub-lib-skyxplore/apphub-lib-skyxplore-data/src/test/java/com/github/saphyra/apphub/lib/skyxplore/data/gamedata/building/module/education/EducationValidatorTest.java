package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.education;

import com.github.saphyra.apphub.lib.common_domain.Range;
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
        ExceptionValidator.validateInvalidParam(() -> underTest.validate(null), "educations", "must not be null");
    }

    @Test
    void blankId() {
        given(education.getId()).willReturn(" ");

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(List.of(education)), "education.id", "must not be null or blank");
    }

    @Test
    void nullSkill() {
        given(education.getId()).willReturn("id");

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(List.of(education)), "education.skill", "must not be null");
    }

    @Test
    void nullLevelLimit() {
        given(education.getId()).willReturn("id");
        given(education.getSkill()).willReturn(SkillType.AIMING);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(List.of(education)), "education.levelLimit", "must not be null");
    }

    @Test
    void minLevelTooLow() {
        given(education.getId()).willReturn("id");
        given(education.getSkill()).willReturn(SkillType.AIMING);
        given(education.getLevelLimit()).willReturn(new Range<>(-1, 1));

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(List.of(education)), "education.levelLimit.min", "too low");
    }

    @Test
    void maxLevelTooLow() {
        given(education.getId()).willReturn("id");
        given(education.getSkill()).willReturn(SkillType.AIMING);
        given(education.getLevelLimit()).willReturn(new Range<>(0, 0));

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(List.of(education)), "education.levelLimit.max", "too low");
    }

    @Test
    void valid() {
        given(education.getId()).willReturn("id");
        given(education.getSkill()).willReturn(SkillType.AIMING);
        given(education.getLevelLimit()).willReturn(new Range<>(0, 1));

        underTest.validate(List.of(education));
    }
}
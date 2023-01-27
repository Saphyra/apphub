package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.skill;

import com.github.saphyra.apphub.api.skyxplore.model.game.SkillModel;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemValidator;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SkillModelValidatorTest {
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final String SKILL_TYPE = "skill-type";
    private static final Integer LEVEL = 354;
    private static final Integer EXPERIENCE = 475;
    private static final Integer NEXT_LEVEL = 5675;

    @Mock
    private GameItemValidator gameItemValidator;

    @InjectMocks
    private SkillModelValidator underTest;

    @Mock
    private SkillModel model;

    @AfterEach
    public void validate() {
        verify(gameItemValidator).validate(model);
    }

    @Test
    public void nullCitizenId() {
        given(model.getCitizenId()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "citizenId", "must not be null");
    }

    @Test
    public void nullSkillType() {
        given(model.getCitizenId()).willReturn(CITIZEN_ID);
        given(model.getSkillType()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "skillType", "must not be null");
    }

    @Test
    public void nullLevel() {
        given(model.getCitizenId()).willReturn(CITIZEN_ID);
        given(model.getSkillType()).willReturn(SKILL_TYPE);
        given(model.getLevel()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "level", "must not be null");
    }

    @Test
    public void nullExperience() {
        given(model.getCitizenId()).willReturn(CITIZEN_ID);
        given(model.getSkillType()).willReturn(SKILL_TYPE);
        given(model.getLevel()).willReturn(LEVEL);
        given(model.getExperience()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "experience", "must not be null");
    }

    @Test
    public void nullNextLevel() {
        given(model.getCitizenId()).willReturn(CITIZEN_ID);
        given(model.getSkillType()).willReturn(SKILL_TYPE);
        given(model.getLevel()).willReturn(LEVEL);
        given(model.getExperience()).willReturn(EXPERIENCE);
        given(model.getNextLevel()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(model));

        ExceptionValidator.validateInvalidParam(ex, "nextLevel", "must not be null");
    }

    @Test
    public void valid() {
        given(model.getCitizenId()).willReturn(CITIZEN_ID);
        given(model.getSkillType()).willReturn(SKILL_TYPE);
        given(model.getLevel()).willReturn(LEVEL);
        given(model.getExperience()).willReturn(EXPERIENCE);
        given(model.getNextLevel()).willReturn(NEXT_LEVEL);

        underTest.validate(model);
    }
}
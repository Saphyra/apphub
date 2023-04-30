package com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SkillsTest {
    private static final UUID CITIZEN_ID = UUID.randomUUID();

    private final Skills underTest = new Skills();

    @Mock
    private Skill skill1;

    @Mock
    private Skill skill2;

    @Test
    void findByCitizenIdAndSkillType_found() {
        given(skill1.getCitizenId()).willReturn(CITIZEN_ID);
        given(skill1.getSkillType()).willReturn(SkillType.AIMING);

        underTest.add(skill1);

        assertThat(underTest.findByCitizenIdAndSkillType(CITIZEN_ID, SkillType.AIMING)).isEqualTo(skill1);
    }

    @Test
    void findByCitizenIdAndSkillType_notFound() {
        given(skill1.getCitizenId()).willReturn(UUID.randomUUID());

        given(skill2.getCitizenId()).willReturn(CITIZEN_ID);
        given(skill2.getSkillType()).willReturn(SkillType.BUILDING);

        underTest.add(skill1);
        underTest.add(skill2);

        Throwable ex = catchThrowable(() -> underTest.findByCitizenIdAndSkillType(CITIZEN_ID, SkillType.AIMING));

        ExceptionValidator.validateLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }
}
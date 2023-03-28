package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.work;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizens;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.CitizenEfficiencyCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SleepTimeCalculatorTest {
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final int WORK_POINTS = 250;
    private static final Integer WORK_POINTS_PER_SECONDS = 50;

    @Mock
    private CitizenEfficiencyCalculator citizenEfficiencyCalculator;

    @Mock
    private GameProperties gameProperties;

    @InjectMocks
    private SleepTimeCalculator underTest;

    @Mock
    private CitizenProperties citizenProperties;

    @Mock
    private Citizen citizen;

    @Mock
    private GameData gameData;

    @Mock
    private Citizens citizens;

    @Test
    public void calculateSleepTime() {
        given(gameProperties.getCitizen()).willReturn(citizenProperties);
        given(citizenProperties.getWorkPointsPerSeconds()).willReturn(WORK_POINTS_PER_SECONDS);
        given(gameData.getCitizens()).willReturn(citizens);
        given(citizens.findByCitizenIdValidated(CITIZEN_ID)).willReturn(citizen);
        given(citizenEfficiencyCalculator.calculateEfficiency(gameData, citizen, SkillType.AIMING)).willReturn(2d);

        long result = underTest.calculateSleepTime(gameData, CITIZEN_ID, SkillType.AIMING, WORK_POINTS);

        assertThat(result).isEqualTo(2500);
    }
}
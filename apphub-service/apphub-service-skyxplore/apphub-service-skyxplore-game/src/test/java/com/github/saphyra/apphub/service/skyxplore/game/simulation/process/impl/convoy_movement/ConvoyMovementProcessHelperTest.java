package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.convoy_movement;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizens;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.CitizenEfficiencyCalculator;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.CitizenUpdateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ConvoyMovementProcessHelperTest {
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final int REQUESTED_WORK_POINTS = 10;

    @Mock
    private GameProperties gameProperties;

    @Mock
    private CitizenEfficiencyCalculator citizenEfficiencyCalculator;

    @Mock
    private CitizenUpdateService citizenUpdateService;

    @InjectMocks
    private ConvoyMovementProcessHelper underTest;

    @Mock
    private GameData gameData;

    @Mock
    private CitizenProperties citizenProperties;

    @Mock
    private Citizens citizens;

    @Mock
    private Citizen citizen;

    @Mock
    private GameProgressDiff progressDiff;

    @Test
    void getWorkPointsPerTick() {
        given(gameData.getCitizens()).willReturn(citizens);
        given(citizens.findByCitizenIdValidated(CITIZEN_ID)).willReturn(citizen);
        given(gameProperties.getCitizen()).willReturn(citizenProperties);
        given(citizenProperties.getWorkPointsPerTick()).willReturn(5);
        given(citizenEfficiencyCalculator.calculateEfficiency(gameData, citizen, SkillType.LOGISTICS)).willReturn(0.8);

        assertThat(underTest.getWorkPointsPerTick(gameData, CITIZEN_ID, REQUESTED_WORK_POINTS)).isEqualTo(4);
    }

    @Test
    void work() {
        underTest.work(progressDiff, gameData, CITIZEN_ID, SkillType.LOGISTICS, REQUESTED_WORK_POINTS);

        then(citizenUpdateService).should().updateCitizen(progressDiff, gameData, CITIZEN_ID, REQUESTED_WORK_POINTS, SkillType.LOGISTICS);
    }
}
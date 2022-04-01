package com.github.saphyra.apphub.service.skyxplore.game.tick.work;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.CitizenEfficiencyCalculator;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.Assignment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MakeCitizenWorkServiceTest {
    public static final int WORK_POINTS_LEFT = 3;
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final int REQUESTED_WORK_POINTS = 100;
    private static final Double CITIZEN_EFFICIENCY = 2d;

    @Mock
    private CitizenEfficiencyCalculator citizenEfficiencyCalculator;

    @Mock
    private UseCitizenWorkPointsService useCitizenWorkPointsService;

    @InjectMocks
    private MakeCitizenWorkService underTest;

    @Mock
    private Assignment assignment;

    @Mock
    private Citizen citizen;

    @Test
    public void makeCitizenWork() {
        given(assignment.getCitizen()).willReturn(citizen);
        given(assignment.getWorkPointsLeft()).willReturn(WORK_POINTS_LEFT);

        given(citizenEfficiencyCalculator.calculateEfficiency(citizen, SkillType.AIMING)).willReturn(CITIZEN_EFFICIENCY);

        int result = underTest.requestWork(GAME_ID, USER_ID, PLANET_ID, assignment, REQUESTED_WORK_POINTS, SkillType.AIMING);

        verify(assignment).reduceWorkPoints(WORK_POINTS_LEFT);
        verify(useCitizenWorkPointsService).useWorkPoints(GAME_ID, USER_ID, PLANET_ID, citizen, WORK_POINTS_LEFT, SkillType.AIMING);

        assertThat(result).isEqualTo((int) (WORK_POINTS_LEFT * CITIZEN_EFFICIENCY));
    }
}
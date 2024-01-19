package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.rest;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.Priorities;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.Priority;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.PriorityType;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessParamKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class RestProcessTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final Integer REST_FOR_TICKS = 1;
    private static final Integer RESTED_FOR_TICKS = 0;
    private static final Double MORALE_BASED_MULTIPLIER = 0.5;
    private static final Integer PLANET_PRIORITY = 23;
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private GameData gameData;

    @Mock
    private RestProcessHelper helper;

    @Mock
    private RestProcessConditions conditions;

    @Mock
    private Game game;

    private RestProcess underTest;

    @Mock
    private Priorities priorities;

    @Mock
    private Priority priority;

    @Mock
    private GameProgressDiff progressDiff;

    @BeforeEach
    void setUp() {
        underTest = RestProcess.builder()
            .processId(PROCESS_ID)
            .status(ProcessStatus.CREATED)
            .gameData(gameData)
            .citizenId(CITIZEN_ID)
            .location(LOCATION)
            .restForTicks(REST_FOR_TICKS)
            .restedForTicks(RESTED_FOR_TICKS)
            .applicationContextProxy(applicationContextProxy)
            .game(game)
            .build();
    }

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.REST);
    }

    @Test
    void getExternalReference() {
        assertThat(underTest.getExternalReference()).isEqualTo(CITIZEN_ID);
    }

    @Test
    void getPriority() {
        given(applicationContextProxy.getBean(RestProcessHelper.class)).willReturn(helper);
        given(helper.getMoraleBasedMultiplier(gameData, CITIZEN_ID)).willReturn(MORALE_BASED_MULTIPLIER);
        given(gameData.getPriorities()).willReturn(priorities);
        given(priorities.findByLocationAndType(LOCATION, PriorityType.WELL_BEING)).willReturn(priority);
        given(priority.getValue()).willReturn(PLANET_PRIORITY);

        assertThat(underTest.getPriority()).isEqualTo((int) (MORALE_BASED_MULTIPLIER * PLANET_PRIORITY * 5 * GameConstants.PROCESS_PRIORITY_MULTIPLIER));
    }

    @Test
    void work_citizenAllocated() {
        given(applicationContextProxy.getBean(RestProcessConditions.class)).willReturn(conditions);
        given(conditions.citizenAllocated(gameData, CITIZEN_ID)).willReturn(true);

        underTest.work();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.READY_TO_DELETE);

        verify(applicationContextProxy, times(0)).getBean(RestProcessHelper.class);
    }

    @Test
    void work() {
        given(applicationContextProxy.getBean(RestProcessConditions.class)).willReturn(conditions);
        given(applicationContextProxy.getBean(RestProcessHelper.class)).willReturn(helper);
        given(conditions.citizenAllocated(gameData, CITIZEN_ID))
            .willReturn(false)
            .willReturn(false)
            .willReturn(true);
        given(game.getProgressDiff()).willReturn(progressDiff);


        underTest.work();
        underTest.work();

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.READY_TO_DELETE);

        verify(helper).allocateCitizen(progressDiff, gameData, PROCESS_ID, CITIZEN_ID);
        verify(helper).increaseMorale(progressDiff, gameData, CITIZEN_ID);
    }

    @Test
    void cleanup() {
        given(applicationContextProxy.getBean(RestProcessHelper.class)).willReturn(helper);
        given(game.getProgressDiff()).willReturn(progressDiff);

        underTest.cleanup();

        verify(helper).releaseCitizen(progressDiff, gameData, PROCESS_ID);
    }

    @Test
    void toModel() {
        given(gameData.getGameId()).willReturn(GAME_ID);

        ProcessModel result = underTest.toModel();

        assertThat(result.getId()).isEqualTo(PROCESS_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.PROCESS);
        assertThat(result.getProcessType()).isEqualTo(ProcessType.REST);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.CREATED);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getExternalReference()).isEqualTo(CITIZEN_ID);
        assertThat(result.getData()).containsEntry(ProcessParamKeys.REST_FOR_TICKS, String.valueOf(REST_FOR_TICKS));
        assertThat(result.getData()).containsEntry(ProcessParamKeys.RESTED_FOR_TICKS, String.valueOf(RESTED_FOR_TICKS));
    }
}
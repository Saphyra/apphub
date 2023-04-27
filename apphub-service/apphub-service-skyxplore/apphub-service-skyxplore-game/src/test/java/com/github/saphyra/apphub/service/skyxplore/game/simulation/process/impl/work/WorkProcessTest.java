package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessParamKeys;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WorkProcessTest {
    private static final int REQUIRED_WORK_POINTS = 5;
    private static final UUID TARGET_ID = UUID.randomUUID();
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final Integer PRIORITY = 234;
    private static final Integer FINISHED_WORK = 3;
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private GameData gameData;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private WorkProcessHelper helper;

    @Mock
    private WorkProcessConditions conditions;

    @Mock
    private SyncCache syncCache;

    private final UuidConverter uuidConverter = new UuidConverter();

    private WorkProcess underTest;

    @Mock
    private Processes processes;

    @Mock
    private Process process;

    @BeforeEach
    void setUp() {
        underTest = WorkProcess.builder()
            .processId(PROCESS_ID)
            .status(ProcessStatus.CREATED)
            .externalReference(EXTERNAL_REFERENCE)
            .skillType(SkillType.AIMING)
            .requiredWorkPoints(REQUIRED_WORK_POINTS)
            .completedWorkPoints(0)
            .workProcessType(WorkProcessType.TERRAFORMATION)
            .targetId(TARGET_ID)
            .gameData(gameData)
            .location(LOCATION)
            .applicationContextProxy(applicationContextProxy)
            .build();
    }

    @Test
    void getPriority() {
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.findByIdValidated(EXTERNAL_REFERENCE)).willReturn(process);
        given(process.getPriority()).willReturn(PRIORITY);

        assertThat(underTest.getPriority()).isEqualTo(PRIORITY + 1);
    }

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.WORK);
    }

    @Test
    void work_buildingNotAllocated() {
        given(applicationContextProxy.getBean(WorkProcessHelper.class)).willReturn(helper);
        given(applicationContextProxy.getBean(WorkProcessConditions.class)).willReturn(conditions);
        given(conditions.hasBuildingAllocated(gameData, PROCESS_ID)).willReturn(false);

        underTest.work(syncCache);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.CREATED);

        verify(helper).allocateParentAsBuildingIfPossible(syncCache, gameData, PROCESS_ID, EXTERNAL_REFERENCE);
        verify(conditions, times(0)).hasCitizenAllocated(gameData, PROCESS_ID);
    }

    @Test
    void work_citizenNotAllocated() {
        given(applicationContextProxy.getBean(WorkProcessHelper.class)).willReturn(helper);
        given(applicationContextProxy.getBean(WorkProcessConditions.class)).willReturn(conditions);
        given(conditions.hasBuildingAllocated(gameData, PROCESS_ID)).willReturn(true);
        given(conditions.hasCitizenAllocated(gameData, PROCESS_ID)).willReturn(false);

        underTest.work(syncCache);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);

        verify(helper).allocateParentAsBuildingIfPossible(syncCache, gameData, PROCESS_ID, EXTERNAL_REFERENCE);
        verify(helper).allocateCitizenIfPossible(syncCache, gameData, PROCESS_ID, LOCATION, SkillType.AIMING, REQUIRED_WORK_POINTS);
        verify(helper, times(0)).work(eq(syncCache), eq(gameData), eq(PROCESS_ID), eq(SkillType.AIMING), anyInt(), eq(WorkProcessType.TERRAFORMATION), eq(TARGET_ID));
    }

    @Test
    void work() {
        given(applicationContextProxy.getBean(WorkProcessHelper.class)).willReturn(helper);
        given(applicationContextProxy.getBean(WorkProcessConditions.class)).willReturn(conditions);
        given(conditions.hasBuildingAllocated(gameData, PROCESS_ID)).willReturn(true);
        given(conditions.hasCitizenAllocated(gameData, PROCESS_ID)).willReturn(true);
        given(helper.work(syncCache, gameData, PROCESS_ID, SkillType.AIMING, REQUIRED_WORK_POINTS, WorkProcessType.TERRAFORMATION, TARGET_ID)).willReturn(FINISHED_WORK);

        underTest.work(syncCache);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);

        verify(helper).allocateParentAsBuildingIfPossible(syncCache, gameData, PROCESS_ID, EXTERNAL_REFERENCE);
        verify(helper, times(0)).allocateCitizenIfPossible(syncCache, gameData, PROCESS_ID, LOCATION, SkillType.AIMING, REQUIRED_WORK_POINTS);
        verify(helper, times(0)).releaseBuildingAndCitizen(syncCache, gameData, PROCESS_ID);
    }

    @Test
    void work_finish() {
        given(applicationContextProxy.getBean(WorkProcessHelper.class)).willReturn(helper);
        given(applicationContextProxy.getBean(WorkProcessConditions.class)).willReturn(conditions);
        given(conditions.hasBuildingAllocated(gameData, PROCESS_ID)).willReturn(true);
        given(conditions.hasCitizenAllocated(gameData, PROCESS_ID)).willReturn(true);
        given(helper.work(syncCache, gameData, PROCESS_ID, SkillType.AIMING, REQUIRED_WORK_POINTS, WorkProcessType.TERRAFORMATION, TARGET_ID)).willReturn(REQUIRED_WORK_POINTS);

        underTest.work(syncCache);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.DONE);

        verify(helper).allocateParentAsBuildingIfPossible(syncCache, gameData, PROCESS_ID, EXTERNAL_REFERENCE);
        verify(helper, times(0)).allocateCitizenIfPossible(syncCache, gameData, PROCESS_ID, LOCATION, SkillType.AIMING, REQUIRED_WORK_POINTS);
        verify(helper).releaseBuildingAndCitizen(syncCache, gameData, PROCESS_ID);
    }

    @Test
    void cleanup() {
        given(applicationContextProxy.getBean(WorkProcessHelper.class)).willReturn(helper);
        given(applicationContextProxy.getBean(UuidConverter.class)).willReturn(uuidConverter);

        underTest.cleanup(syncCache);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.READY_TO_DELETE);

        verify(helper).releaseBuildingAndCitizen(syncCache, gameData, PROCESS_ID);
        verify(syncCache).saveGameItem(underTest.toModel());
    }

    @Test
    void toModel() {
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(applicationContextProxy.getBean(UuidConverter.class)).willReturn(uuidConverter);

        ProcessModel result = underTest.toModel();

        assertThat(result.getId()).isEqualTo(PROCESS_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.PROCESS);
        assertThat(result.getProcessType()).isEqualTo(ProcessType.WORK);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.CREATED);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.getData()).containsEntry(ProcessParamKeys.BUILDING_DATA_ID, null);
        assertThat(result.getData()).containsEntry(ProcessParamKeys.SKILL_TYPE, SkillType.AIMING.name());
        assertThat(result.getData()).containsEntry(ProcessParamKeys.REQUIRED_WORK_POINTS, String.valueOf(REQUIRED_WORK_POINTS));
        assertThat(result.getData()).containsEntry(ProcessParamKeys.WORK_PROCESS_TYPE, WorkProcessType.TERRAFORMATION.name());
        assertThat(result.getData()).containsEntry(ProcessParamKeys.TARGET_ID, uuidConverter.convertDomain(TARGET_ID));
        assertThat(result.getData()).containsEntry(ProcessParamKeys.COMPLETED_WORK_POINTS, String.valueOf(0));
    }
}
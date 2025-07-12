package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.convoy;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessParamKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ConvoyProcessTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID CONVOY_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final Integer PRIORITY = 32;
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String CONVOY_ID_STRING = "convoy-id";

    @Mock
    private GameData gameData;

    @Mock
    private Game game;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private UuidConverter uuidConverter;

    private ConvoyProcess underTest;

    @Mock
    private Processes processes;

    @Mock
    private Process process;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private ConvoyProcessHelper helper;

    @BeforeEach
    void setUp() {
        underTest = ConvoyProcess.builder()
            .processId(PROCESS_ID)
            .status(ProcessStatus.CREATED)
            .convoyId(CONVOY_ID)
            .externalReference(EXTERNAL_REFERENCE)
            .game(game)
            .location(LOCATION)
            .applicationContextProxy(applicationContextProxy)
            .build();
    }

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.CONVOY);
    }

    @Test
    void getPriority() {
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.findByIdValidated(EXTERNAL_REFERENCE)).willReturn(process);
        given(process.getPriority()).willReturn(PRIORITY);
        given(game.getData()).willReturn(gameData);

        assertThat(underTest.getPriority()).isEqualTo(PRIORITY + 1);
    }

    @Test
    void work_notArrived() {
        given(applicationContextProxy.getBean(ConvoyProcessHelper.class)).willReturn(helper);
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(helper.move(game, LOCATION, PROCESS_ID, CONVOY_ID)).willReturn(false);
        given(game.getData()).willReturn(gameData);

        underTest.work();

        then(helper).should().loadResources(progressDiff, gameData, CONVOY_ID);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);
    }

    @Test
    void work_waitingForUnload() {
        given(applicationContextProxy.getBean(ConvoyProcessHelper.class)).willReturn(helper);
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(helper.move(game, LOCATION, PROCESS_ID, CONVOY_ID)).willReturn(true);
        given(helper.unloadResources(progressDiff, gameData, CONVOY_ID)).willReturn(false);
        given(game.getData()).willReturn(gameData);

        underTest.work();

        then(helper).should().loadResources(progressDiff, gameData, CONVOY_ID);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);
    }

    @Test
    void work_done() {
        given(applicationContextProxy.getBean(ConvoyProcessHelper.class)).willReturn(helper);
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(helper.move(game, LOCATION, PROCESS_ID, CONVOY_ID)).willReturn(true);
        given(helper.unloadResources(progressDiff, gameData, CONVOY_ID)).willReturn(true);
        given(game.getData()).willReturn(gameData);

        underTest.work();

        then(helper).should().loadResources(progressDiff, gameData, CONVOY_ID);
        then(helper).should().releaseCitizen(progressDiff, gameData, PROCESS_ID);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.DONE);
    }

    @Test
    void cleanup() {
        given(applicationContextProxy.getBean(ConvoyProcessHelper.class)).willReturn(helper);
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getByExternalReference(PROCESS_ID)).willReturn(List.of(process));
        given(applicationContextProxy.getBean(UuidConverter.class)).willReturn(uuidConverter);
        given(game.getData()).willReturn(gameData);

        underTest.cleanup();

        then(helper).should().releaseCitizen(progressDiff, gameData, PROCESS_ID);
        then(helper).should().cleanup(progressDiff, gameData, CONVOY_ID);
        then(process).should().cleanup();
        then(progressDiff).should().save(underTest.toModel());

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.READY_TO_DELETE);
    }

    @Test
    void toModel() {
        given(game.getGameId()).willReturn(GAME_ID);
        given(applicationContextProxy.getBean(UuidConverter.class)).willReturn(uuidConverter);
        given(uuidConverter.convertDomain(CONVOY_ID)).willReturn(CONVOY_ID_STRING);

        assertThat(underTest.toModel())
            .returns(PROCESS_ID, GameItem::getId)
            .returns(GameItemType.PROCESS, GameItem::getType)
            .returns(GAME_ID, GameItem::getGameId)
            .returns(ProcessType.CONVOY, ProcessModel::getProcessType)
            .returns(ProcessStatus.CREATED, ProcessModel::getStatus)
            .returns(LOCATION, ProcessModel::getLocation)
            .returns(EXTERNAL_REFERENCE, ProcessModel::getExternalReference)
            .returns(Map.of(ProcessParamKeys.CONVOY_ID, CONVOY_ID_STRING), ProcessModel::getData);
    }
}
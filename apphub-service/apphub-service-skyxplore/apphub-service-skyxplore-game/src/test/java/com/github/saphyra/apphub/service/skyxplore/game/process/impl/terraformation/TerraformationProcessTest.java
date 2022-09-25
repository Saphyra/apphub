package com.github.saphyra.apphub.service.skyxplore.game.process.impl.terraformation;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.PriorityType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.ProductionOrderProcessFactoryForConstruction;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.UseAllocatedResourceService;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.production_order.ProductionOrderProcess;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcess;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TerraformationProcessTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final int BASE_PRIORITY = 10;
    private static final Integer CONSTRUCTION_PRIORITY = 20;
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();

    private TerraformationProcess underTest;

    @Mock
    private Game game;

    @Mock
    private Planet planet;

    @Mock
    private Surface surface;

    @Mock
    private Construction terraformation;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private SyncCache syncCache;

    @Mock
    private ProductionOrderProcessFactoryForConstruction productionOrderProcessFactoryForConstruction;

    @Mock
    private ProductionOrderProcess productionOrderProcess;

    @Mock
    private Processes processes;

    @Mock
    private ProcessModel processModel;

    @Mock
    private UseAllocatedResourceService useAllocatedResourceService;

    @Mock
    private RequestWorkProcessFactoryForTerraformation requestWorkProcessFactoryForTerraformation;

    @Mock
    private RequestWorkProcess requestWorkProcess;

    @Mock
    private FinishTerraformationService finishTerraformationService;

    @Before
    public void setUp() {
        setUp(ProcessStatus.CREATED);
    }

    public void setUp(ProcessStatus status) {
        underTest = TerraformationProcess.builder()
            .processId(PROCESS_ID)
            .status(status)
            .game(game)
            .planet(planet)
            .surface(surface)
            .terraformation(terraformation)
            .applicationContextProxy(applicationContextProxy)
            .build();
    }

    @Test
    public void getExternalReference() {
        given(terraformation.getConstructionId()).willReturn(CONSTRUCTION_ID);

        UUID result = underTest.getExternalReference();

        assertThat(result).isEqualTo(CONSTRUCTION_ID);
    }

    @Test
    public void getPriority() {
        given(planet.getPriorities()).willReturn(CollectionUtils.singleValueMap(PriorityType.CONSTRUCTION, BASE_PRIORITY));
        given(terraformation.getPriority()).willReturn(CONSTRUCTION_PRIORITY);

        int result = underTest.getPriority();

        assertThat(result).isEqualTo(BASE_PRIORITY * CONSTRUCTION_PRIORITY * GameConstants.PROCESS_PRIORITY_MULTIPLIER);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.TERRAFORMATION);
    }

    @Test
    public void work_createdStatus() {
        given(applicationContextProxy.getBean(ProductionOrderProcessFactoryForConstruction.class)).willReturn(productionOrderProcessFactoryForConstruction);
        given(productionOrderProcessFactoryForConstruction.createProductionOrderProcesses(PROCESS_ID, game, planet, terraformation)).willReturn(List.of(productionOrderProcess));
        given(productionOrderProcess.toModel()).willReturn(processModel);
        given(processes.getByExternalReferenceAndType(PROCESS_ID, ProcessType.PRODUCTION_ORDER)).willReturn(List.of(productionOrderProcess));
        given(productionOrderProcess.getStatus()).willReturn(ProcessStatus.IN_PROGRESS);
        given(game.getProcesses()).willReturn(processes);

        underTest.work(syncCache);

        verify(processes).add(productionOrderProcess);
        verify(syncCache).saveGameItem(processModel);
        verify(processes, times(0)).getByExternalReferenceAndType(PROCESS_ID, ProcessType.REQUEST_WORK);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);
    }

    @Test
    public void work_createRequestWorkProcesses() {
        setUp(ProcessStatus.IN_PROGRESS);
        given(processes.getByExternalReferenceAndType(PROCESS_ID, ProcessType.PRODUCTION_ORDER)).willReturn(List.of(productionOrderProcess));
        given(productionOrderProcess.getStatus()).willReturn(ProcessStatus.DONE);
        given(processes.getByExternalReferenceAndType(PROCESS_ID, ProcessType.REQUEST_WORK)).willReturn(Collections.emptyList());
        given(applicationContextProxy.getBean(UseAllocatedResourceService.class)).willReturn(useAllocatedResourceService);
        given(game.getGameId()).willReturn(GAME_ID);
        given(applicationContextProxy.getBean(RequestWorkProcessFactoryForTerraformation.class)).willReturn(requestWorkProcessFactoryForTerraformation);
        given(requestWorkProcessFactoryForTerraformation.createRequestWorkProcesses(PROCESS_ID, game, planet, surface)).willReturn(List.of(requestWorkProcess));
        given(requestWorkProcess.toModel()).willReturn(processModel);
        given(game.getProcesses()).willReturn(processes);
        given(terraformation.getConstructionId()).willReturn(CONSTRUCTION_ID);

        underTest.work(syncCache);

        verify(useAllocatedResourceService).resolveAllocations(syncCache, GAME_ID, planet, CONSTRUCTION_ID);
        verify(processes).addAll(List.of(requestWorkProcess));
        verify(syncCache).saveGameItem(processModel);
        verify(applicationContextProxy, times(0)).getBean(FinishTerraformationService.class);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);
    }

    @Test
    public void work_waitingForRequestWorkProcesses() {
        setUp(ProcessStatus.IN_PROGRESS);
        given(game.getProcesses()).willReturn(processes);
        given(processes.getByExternalReferenceAndType(PROCESS_ID, ProcessType.PRODUCTION_ORDER)).willReturn(List.of(productionOrderProcess));
        given(productionOrderProcess.getStatus()).willReturn(ProcessStatus.DONE);
        given(processes.getByExternalReferenceAndType(PROCESS_ID, ProcessType.REQUEST_WORK)).willReturn(List.of(requestWorkProcess));
        given(requestWorkProcess.getStatus()).willReturn(ProcessStatus.IN_PROGRESS);

        underTest.work(syncCache);

        verify(applicationContextProxy, times(0)).getBean(FinishTerraformationService.class);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);
    }

    @Test
    public void work_finishConstruction() {
        setUp(ProcessStatus.IN_PROGRESS);
        given(game.getProcesses()).willReturn(processes);
        given(processes.getByExternalReferenceAndType(PROCESS_ID, ProcessType.PRODUCTION_ORDER)).willReturn(List.of(productionOrderProcess));
        given(productionOrderProcess.getStatus()).willReturn(ProcessStatus.DONE);
        given(processes.getByExternalReferenceAndType(PROCESS_ID, ProcessType.REQUEST_WORK)).willReturn(List.of(requestWorkProcess));
        given(requestWorkProcess.getStatus()).willReturn(ProcessStatus.DONE);
        given(applicationContextProxy.getBean(FinishTerraformationService.class)).willReturn(finishTerraformationService);

        underTest.work(syncCache);

        verify(finishTerraformationService).finishTerraformation(syncCache, game, planet, surface);
        verify(productionOrderProcess).cleanup(syncCache);
        verify(requestWorkProcess).cleanup(syncCache);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.READY_TO_DELETE);
    }

    @Test
    public void cancel() {
        given(game.getProcesses()).willReturn(processes);
        given(processes.getByExternalReference(PROCESS_ID)).willReturn(List.of(productionOrderProcess));

        underTest.cancel(syncCache);

        verify(productionOrderProcess).cancel(syncCache);
        verify(syncCache).saveGameItem(any(ProcessModel.class));

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.READY_TO_DELETE);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void cleanup() {
        underTest.cleanup(syncCache);
    }

    @Test
    public void toModel() {
        given(game.getGameId()).willReturn(GAME_ID);
        given(planet.getPlanetId()).willReturn(PLANET_ID);
        given(terraformation.getConstructionId()).willReturn(CONSTRUCTION_ID);

        ProcessModel result = underTest.toModel();

        assertThat(result.getId()).isEqualTo(PROCESS_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.PROCESS);
        assertThat(result.getProcessType()).isEqualTo(ProcessType.TERRAFORMATION);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.CREATED);
        assertThat(result.getLocation()).isEqualTo(PLANET_ID);
        assertThat(result.getLocationType()).isEqualTo(LocationType.PLANET.name());
        assertThat(result.getExternalReference()).isEqualTo(CONSTRUCTION_ID);
    }
}
package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.terraformation;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.SurfaceModel;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.SurfaceConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surfaces;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.AllocationRemovalService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.UseAllocatedResourceService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order.ProductionOrderService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work.WorkProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work.WorkProcessFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TerraformationProcessHelperTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID TERRAFORMATION_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final int REQUIRED_WORK_POINTS = 25;
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private UseAllocatedResourceService useAllocatedResourceService;

    @Mock
    private WorkProcessFactory workProcessFactory;

    @Mock
    private AllocationRemovalService allocationRemovalService;

    @Mock
    private ProductionOrderService productionOrderService;

    @Mock
    private SurfaceConverter surfaceConverter;

    @InjectMocks
    private TerraformationProcessHelper underTest;

    @Mock
    private SyncCache syncCache;

    @Mock
    private GameData gameData;

    @Mock
    private Construction terraformation;

    @Mock
    private Constructions constructions;

    @Mock
    private WorkProcess workProcess;

    @Mock
    private ProcessModel processModel;

    @Mock
    private Processes processes;

    @Mock
    private Surfaces surfaces;

    @Mock
    private Surface surface;

    @Mock
    private SurfaceModel surfaceModel;

    @Test
    void startWork() {
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByConstructionIdValidated(TERRAFORMATION_ID)).willReturn(terraformation);
        given(terraformation.getLocation()).willReturn(LOCATION);
        given(terraformation.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);
        given(workProcessFactory.createForTerraformation(gameData, PROCESS_ID, TERRAFORMATION_ID, LOCATION, REQUIRED_WORK_POINTS)).willReturn(List.of(workProcess));
        given(gameData.getProcesses()).willReturn(processes);
        given(workProcess.toModel()).willReturn(processModel);

        underTest.startWork(syncCache, gameData, PROCESS_ID, TERRAFORMATION_ID);

        verify(useAllocatedResourceService).resolveAllocations(syncCache, gameData, LOCATION, TERRAFORMATION_ID);
        verify(processes).add(workProcess);
        verify(syncCache).saveGameItem(processModel);
    }

    @Test
    void finishTerraformation() {
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByConstructionIdValidated(TERRAFORMATION_ID)).willReturn(terraformation);
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(terraformation.getExternalReference()).willReturn(SURFACE_ID);
        given(surfaces.findBySurfaceId(SURFACE_ID)).willReturn(surface);
        given(terraformation.getData()).willReturn(SurfaceType.CONCRETE.name());
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(surfaceConverter.toModel(GAME_ID, surface)).willReturn(surfaceModel);
        given(terraformation.getConstructionId()).willReturn(TERRAFORMATION_ID);

        underTest.finishTerraformation(syncCache, gameData, TERRAFORMATION_ID);

        verify(allocationRemovalService).removeAllocationsAndReservations(syncCache, gameData, TERRAFORMATION_ID);
        verify(surface).setSurfaceType(SurfaceType.CONCRETE);
        verify(constructions).remove(terraformation);
        verify(syncCache).deleteGameItem(TERRAFORMATION_ID, GameItemType.CONSTRUCTION);
        then(syncCache).should().saveGameItem(surfaceModel);
    }

    @Test
    void createProductionOrders() {
        underTest.createProductionOrders(syncCache, gameData, PROCESS_ID, TERRAFORMATION_ID);

        verify(productionOrderService).createProductionOrdersForReservedStorages(syncCache, gameData, PROCESS_ID, TERRAFORMATION_ID);
    }
}
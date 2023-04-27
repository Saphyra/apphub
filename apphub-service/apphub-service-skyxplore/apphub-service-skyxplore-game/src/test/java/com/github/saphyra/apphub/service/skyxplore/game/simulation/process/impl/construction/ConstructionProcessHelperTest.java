package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Buildings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConstructionProcessHelperTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final int REQUIRED_WORK_POINTS = 3142;
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();

    @Mock
    private UseAllocatedResourceService useAllocatedResourceService;

    @Mock
    private WorkProcessFactory workProcessFactory;

    @Mock
    private AllocationRemovalService allocationRemovalService;

    @Mock
    private ProductionOrderService productionOrderService;

    @InjectMocks
    private ConstructionProcessHelper underTest;

    @Mock
    private SyncCache syncCache;

    @Mock
    private GameData gameData;

    @Mock
    private Constructions constructions;

    @Mock
    private Construction construction;

    @Mock
    private Planet planet;

    @Mock
    private WorkProcess workProcess;

    @Mock
    private ProcessModel processModel;

    @Mock
    private Processes processes;

    @Mock
    private Buildings buildings;

    @Mock
    private Building building;

    @Mock
    private Surfaces surfaces;

    @Mock
    private Surface surface;

    @Test
    void startWork() {
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByConstructionIdValidated(CONSTRUCTION_ID)).willReturn(construction);
        given(construction.getLocation()).willReturn(LOCATION);
        given(gameData.getPlanets()).willReturn(CollectionUtils.singleValueMap(LOCATION, planet, new Planets()));
        given(planet.getOwner()).willReturn(OWNER_ID);
        given(construction.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);
        given(workProcessFactory.createForConstruction(gameData, PROCESS_ID, CONSTRUCTION_ID, LOCATION, REQUIRED_WORK_POINTS)).willReturn(List.of(workProcess));
        given(gameData.getProcesses()).willReturn(processes);
        given(workProcess.toModel()).willReturn(processModel);

        underTest.startWork(syncCache, gameData, PROCESS_ID, CONSTRUCTION_ID);

        verify(useAllocatedResourceService).resolveAllocations(syncCache, gameData, LOCATION, OWNER_ID, CONSTRUCTION_ID);
        verify(processes).add(workProcess);
        verify(syncCache).saveGameItem(processModel);
    }

    @Test
    void finishConstruction() {
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByConstructionIdValidated(CONSTRUCTION_ID)).willReturn(construction);
        given(construction.getLocation()).willReturn(LOCATION);
        given(gameData.getPlanets()).willReturn(CollectionUtils.singleValueMap(LOCATION, planet, new Planets()));
        given(gameData.getBuildings()).willReturn(buildings);
        given(construction.getExternalReference()).willReturn(BUILDING_ID);
        given(buildings.findByBuildingId(BUILDING_ID)).willReturn(building);
        given(building.getSurfaceId()).willReturn(SURFACE_ID);
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(surfaces.findBySurfaceId(SURFACE_ID)).willReturn(surface);
        given(planet.getOwner()).willReturn(OWNER_ID);

        underTest.finishConstruction(syncCache, gameData, CONSTRUCTION_ID);

        verify(building).increaseLevel();
        verify(constructions).remove(construction);
        verify(allocationRemovalService).removeAllocationsAndReservations(syncCache, gameData, LOCATION, OWNER_ID, CONSTRUCTION_ID);
        verify(syncCache).constructionFinished(OWNER_ID, LOCATION, construction, building, surface);
    }

    @Test
    void createProductionOrders() {
        underTest.createProductionOrders(syncCache, gameData, PROCESS_ID, CONSTRUCTION_ID);

        verify(productionOrderService).createProductionOrdersForReservedStorages(syncCache, gameData, PROCESS_ID, CONSTRUCTION_ID);
    }
}
package com.github.saphyra.apphub.service.skyxplore.game.tick.planet.processor.construction;

import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.tick.production.AllocatedResourceResolver;
import com.github.saphyra.apphub.service.skyxplore.game.tick.production.ProduceResourcesService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@RunWith(MockitoJUnitRunner.class)
public class ConstructionTickProcessorTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final Integer REQUIRED_WORK_POINTS = 413;

    @Mock
    private FinishConstructionService finishConstructionService;

    @Mock
    private ProceedWithConstructionService proceedWithConstructionService;

    @Mock
    private ProduceResourcesService produceResourcesService;

    @Mock
    private AllocatedResourceResolver allocatedResourceResolver;

    @InjectMocks
    private ConstructionTickProcessor underTest;

    @Mock
    private Planet planet;

    @Mock
    private Surface surface;

    @Mock
    private Building building;

    @Mock
    private Construction construction;

    @Mock
    private StorageDetails storageDetails;

    @Mock
    private ReservedStorage reservedStorage;

    @Before
    public void setUp() {
        given(surface.getBuilding()).willReturn(building);
        given(building.getConstruction()).willReturn(construction);
        given(planet.getStorageDetails()).willReturn(storageDetails);
        given(storageDetails.getReservedStorages()).willReturn(new ReservedStorages(List.of(reservedStorage)));
        given(construction.getConstructionId()).willReturn(CONSTRUCTION_ID);
        given(reservedStorage.getExternalReference()).willReturn(CONSTRUCTION_ID);
        given(reservedStorage.getAmount()).willReturn(0);
        given(construction.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);
    }

    @Test
    public void missingResources() {
        given(reservedStorage.getAmount()).willReturn(1);

        underTest.process(GAME_ID, planet, surface);

        verify(produceResourcesService).produceResources(GAME_ID, planet, CONSTRUCTION_ID);
        verifyNoInteractions(allocatedResourceResolver);
        verifyNoInteractions(proceedWithConstructionService);
        verifyNoInteractions(finishConstructionService);
    }

    @Test
    public void zeroCurrentWorkPoints() {
        given(construction.getCurrentWorkPoints()).willReturn(0);

        underTest.process(GAME_ID, planet, surface);

        verifyNoInteractions(produceResourcesService);
        verify(allocatedResourceResolver).resolveAllocations(GAME_ID, planet, CONSTRUCTION_ID);
        verify(proceedWithConstructionService).proceedWithConstruction(GAME_ID, planet, surface, construction);
        verifyNoInteractions(finishConstructionService);
    }

    @Test
    public void constructionFinished() {
        given(construction.getCurrentWorkPoints()).willReturn(REQUIRED_WORK_POINTS);

        underTest.process(GAME_ID, planet, surface);

        verifyNoInteractions(produceResourcesService);
        verifyNoInteractions(allocatedResourceResolver);
        verify(proceedWithConstructionService).proceedWithConstruction(GAME_ID, planet, surface, construction);
        verify(finishConstructionService).finishConstruction(GAME_ID, planet, surface);
    }
}
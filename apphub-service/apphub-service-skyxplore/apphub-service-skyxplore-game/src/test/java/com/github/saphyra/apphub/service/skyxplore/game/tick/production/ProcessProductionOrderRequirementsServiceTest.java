package com.github.saphyra.apphub.service.skyxplore.game.tick.production;

import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.ProductionOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ProcessProductionOrderRequirementsServiceTest {
    private static final UUID PRODUCTION_ORDER_ID = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private ProductionOrderProcessingService productionOrderProcessingService;

    @InjectMocks
    private ProcessProductionOrderRequirementsService underTest;

    @Mock
    private Planet planet;

    @Mock
    private ProductionOrder inputOrder;

    @Mock
    private ReservedStorage anotherReservedStorage;

    @Mock
    private ReservedStorage completedReservedStorage;

    @Mock
    private ReservedStorage toBeDoneReservedStorage;

    @Mock
    private StorageDetails storageDetails;

    @Mock
    private ProductionOrder anotherOrder;

    @Mock
    private ProductionOrder childOrder;

    @Test
    public void processRequirements() {
        given(planet.getOrders()).willReturn(Set.of(anotherOrder, childOrder));
        given(planet.getStorageDetails()).willReturn(storageDetails);
        given(storageDetails.getReservedStorages()).willReturn(new ReservedStorages(List.of(anotherReservedStorage, completedReservedStorage, toBeDoneReservedStorage)));

        given(inputOrder.getProductionOrderId()).willReturn(PRODUCTION_ORDER_ID);

        given(anotherReservedStorage.getExternalReference()).willReturn(UUID.randomUUID());
        given(completedReservedStorage.getExternalReference()).willReturn(PRODUCTION_ORDER_ID);
        given(completedReservedStorage.getAmount()).willReturn(0);

        given(toBeDoneReservedStorage.getExternalReference()).willReturn(PRODUCTION_ORDER_ID);
        given(toBeDoneReservedStorage.getAmount()).willReturn(10);
        given(toBeDoneReservedStorage.getReservedStorageId()).willReturn(RESERVED_STORAGE_ID);

        given(anotherOrder.getExternalReference()).willReturn(UUID.randomUUID());
        given(childOrder.getExternalReference()).willReturn(RESERVED_STORAGE_ID);

        underTest.processRequirements(GAME_ID, planet, inputOrder, productionOrderProcessingService);

        verify(productionOrderProcessingService).processOrder(GAME_ID, planet, childOrder);
    }
}
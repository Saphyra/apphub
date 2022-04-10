package com.github.saphyra.apphub.service.skyxplore.game.process.impl.construction;

import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.production_order.ProductionOrderProcess;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.production_order.ProductionOrderProcessFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ProductionOrderProcessFactoryForConstructionTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();

    @Mock
    private ProductionOrderProcessFactory productionOrderProcessFactory;

    @InjectMocks
    private ProductionOrderProcessFactoryForConstruction underTest;

    @Mock
    private Game game;


    @Mock

    private Planet planet;

    @Mock
    private Construction construction;

    @Mock
    private StorageDetails storageDetails;

    @Mock
    private ReservedStorage reservedStorage;

    @Mock
    private ProductionOrderProcess productionOrderProcess;

    @Test
    public void createProductionOrderProcesses() {
        given(planet.getStorageDetails()).willReturn(storageDetails);
        given(storageDetails.getReservedStorages()).willReturn(new ReservedStorages(List.of(reservedStorage)));
        given(reservedStorage.getExternalReference()).willReturn(CONSTRUCTION_ID);
        given(construction.getConstructionId()).willReturn(CONSTRUCTION_ID);
        given(reservedStorage.getReservedStorageId()).willReturn(RESERVED_STORAGE_ID);
        given(productionOrderProcessFactory.create(PROCESS_ID, game, planet, RESERVED_STORAGE_ID)).willReturn(List.of(productionOrderProcess));

        List<ProductionOrderProcess> result = underTest.createProductionOrderProcesses(PROCESS_ID, game, planet, construction);

        assertThat(result).containsExactly(productionOrderProcess);
    }
}
package com.github.saphyra.apphub.service.skyxplore.game.process.impl;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.production_order.ProductionOrderProcess;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.production_order.ProductionOrderProcessFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ProductionOrderProcessFactoryForConstructionTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();

    @Mock
    private ProductionOrderProcessFactory productionOrderProcessFactory;

    @InjectMocks
    private ProductionOrderProcessFactoryForConstruction underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Planet planet;

    @Mock
    private Construction construction;

    @Mock
    private ReservedStorage reservedStorage;

    @Mock
    private ProductionOrderProcess productionOrderProcess;

    @Mock
    private ReservedStorages reservedStorages;

    @Test
    public void createProductionOrderProcesses() {
        given(gameData.getReservedStorages()).willReturn(reservedStorages);
        given(reservedStorages.getByExternalReference(CONSTRUCTION_ID)).willReturn(List.of(reservedStorage));
        given(reservedStorage.getExternalReference()).willReturn(CONSTRUCTION_ID);
        given(construction.getConstructionId()).willReturn(CONSTRUCTION_ID);
        given(reservedStorage.getReservedStorageId()).willReturn(RESERVED_STORAGE_ID);
        given(productionOrderProcessFactory.create(gameData, PROCESS_ID, LOCATION, RESERVED_STORAGE_ID)).willReturn(List.of(productionOrderProcess));

        List<ProductionOrderProcess> result = underTest.createProductionOrderProcesses(PROCESS_ID, gameData, LOCATION, construction);

        assertThat(result).containsExactly(productionOrderProcess);
    }
}
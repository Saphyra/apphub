package com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.assignment.data_provider;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order.ProductionOrderProcess;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductionOrderCitizenAssignmentDataProviderTest {
    private static final String DATA_ID = "data-id";
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();

    @InjectMocks
    private ProductionOrderCitizenAssignmentDataProvider underTest;

    @Mock
    private GameData gameData;

    @Mock
    private ProductionOrderProcess process;

    @Mock
    private ReservedStorages reservedStorages;

    @Mock
    private ReservedStorage reservedStorage;

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.PRODUCTION_ORDER);
    }

    @Test
    void getData() {
        given(gameData.getReservedStorages()).willReturn(reservedStorages);
        given(process.getReservedStorageId()).willReturn(RESERVED_STORAGE_ID);
        given(reservedStorages.findByIdValidated(RESERVED_STORAGE_ID)).willReturn(reservedStorage);
        given(reservedStorage.getDataId()).willReturn(DATA_ID);

        assertThat(underTest.getData(gameData, process)).isEqualTo(DATA_ID);
    }
}
package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.consumption;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResources;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.AllocatedResourceFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.ReservedStorageFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ConsumptionCalculatorTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer AMOUNT = 340;
    private static final Integer STORED_AMOUNT = 100;
    private static final Integer ALLOCATED_AMOUNT = 50;
    private static final UUID PLANET_ID = UUID.randomUUID();

    @Mock
    private AllocatedResourceFactory allocatedResourceFactory;

    @Mock
    private ReservedStorageFactory reservedStorageFactory;

    @InjectMocks
    private ConsumptionCalculator underTest;

    @Mock
    private Planet planet;

    @Mock
    private GameData gameData;

    @Mock
    private StoredResource storedResource;

    @Mock
    private AllocatedResource allocatedResource;

    @Mock
    private AllocatedResource createdAllocatedResource;

    @Mock
    private ReservedStorage createdReservedStorage;

    @Mock
    private StoredResources storedResources;

    @Mock
    private AllocatedResources allocatedResources;

    @Test
    public void calculate() {
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storedResources.findByLocationAndDataId(PLANET_ID, DATA_ID)).willReturn(Optional.of(storedResource));
        given(storedResource.getAmount()).willReturn(STORED_AMOUNT);

        given(gameData.getAllocatedResources()).willReturn(allocatedResources);
        given(allocatedResources.getByLocationAndDataId(PLANET_ID, DATA_ID)).willReturn(List.of(allocatedResource));
        given(allocatedResource.getAmount()).willReturn(ALLOCATED_AMOUNT);

        given(allocatedResource.getDataId()).willReturn(DATA_ID);
        given(allocatedResource.getAmount()).willReturn(ALLOCATED_AMOUNT);
        given(planet.getPlanetId()).willReturn(PLANET_ID);

        given(allocatedResourceFactory.create(PLANET_ID, EXTERNAL_REFERENCE, DATA_ID, STORED_AMOUNT - ALLOCATED_AMOUNT)).willReturn(createdAllocatedResource);
        given(reservedStorageFactory.create(PLANET_ID, EXTERNAL_REFERENCE, DATA_ID, AMOUNT - (STORED_AMOUNT - ALLOCATED_AMOUNT))).willReturn(createdReservedStorage);

        ConsumptionResult result = underTest.calculate(gameData, PLANET_ID, EXTERNAL_REFERENCE, DATA_ID, AMOUNT);

        assertThat(result.getReservation()).isEqualTo(createdReservedStorage);
        assertThat(result.getAllocation()).isEqualTo(createdAllocatedResource);
    }
}
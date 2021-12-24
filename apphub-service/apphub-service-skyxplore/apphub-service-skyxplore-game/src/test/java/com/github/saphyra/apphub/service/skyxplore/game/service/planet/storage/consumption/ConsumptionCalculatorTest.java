package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.consumption;

import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.ReservedStorageFactory;
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
    private StorageDetails storageDetails;

    @Mock
    private StoredResource storedResource;

    @Mock
    private AllocatedResource allocatedResource;

    @Mock
    private AllocatedResource createdAllocatedResource;

    @Mock
    private ReservedStorage createdReservedStorage;

    @Test
    public void calculate() {
        given(planet.getStorageDetails()).willReturn(storageDetails);
        given(storageDetails.getStoredResources()).willReturn(CollectionUtils.singleValueMap(DATA_ID, storedResource));
        given(storedResource.getAmount()).willReturn(STORED_AMOUNT);
        given(storageDetails.getAllocatedResources()).willReturn(new AllocatedResources(List.of(allocatedResource)));
        given(allocatedResource.getDataId()).willReturn(DATA_ID);
        given(allocatedResource.getAmount()).willReturn(ALLOCATED_AMOUNT);
        given(planet.getPlanetId()).willReturn(PLANET_ID);

        given(allocatedResourceFactory.create(PLANET_ID, LocationType.PLANET, EXTERNAL_REFERENCE, DATA_ID, STORED_AMOUNT - ALLOCATED_AMOUNT)).willReturn(createdAllocatedResource);
        given(reservedStorageFactory.create(PLANET_ID, LocationType.PLANET, EXTERNAL_REFERENCE, DATA_ID, AMOUNT - (STORED_AMOUNT - ALLOCATED_AMOUNT))).willReturn(createdReservedStorage);

        ConsumptionResult result = underTest.calculate(planet, LocationType.PLANET, EXTERNAL_REFERENCE, DATA_ID, AMOUNT);

        assertThat(result.getReservation()).isEqualTo(createdReservedStorage);
        assertThat(result.getAllocation()).isEqualTo(createdAllocatedResource);
    }
}
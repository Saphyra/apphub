package com.github.saphyra.apphub.service.skyxplore.game.tick.production;

import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
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
public class ProductionOrderRequirementsMetCalculatorTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();

    @InjectMocks
    private ProductionOrderRequirementsMetCalculator underTest;

    @Mock
    private ReservedStorage anotherReservedStorage;

    @Mock
    private ReservedStorage reservedStorage;

    @Mock
    private StorageDetails storageDetails;

    @Mock
    private Planet planet;

    @Test
    public void requirementsMet() {
        given(planet.getStorageDetails()).willReturn(storageDetails);
        given(storageDetails.getReservedStorages()).willReturn(new ReservedStorages(List.of(anotherReservedStorage, reservedStorage)));

        given(anotherReservedStorage.getExternalReference()).willReturn(UUID.randomUUID());
        given(reservedStorage.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(reservedStorage.getAmount()).willReturn(0);

        boolean result = underTest.areRequirementsMet(planet, EXTERNAL_REFERENCE);

        assertThat(result).isTrue();
    }

    @Test
    public void requirementsNotMet() {
        given(planet.getStorageDetails()).willReturn(storageDetails);
        given(storageDetails.getReservedStorages()).willReturn(new ReservedStorages(List.of(anotherReservedStorage, reservedStorage)));

        given(anotherReservedStorage.getExternalReference()).willReturn(UUID.randomUUID());
        given(reservedStorage.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(reservedStorage.getAmount()).willReturn(1);

        boolean result = underTest.areRequirementsMet(planet, EXTERNAL_REFERENCE);

        assertThat(result).isFalse();
    }
}
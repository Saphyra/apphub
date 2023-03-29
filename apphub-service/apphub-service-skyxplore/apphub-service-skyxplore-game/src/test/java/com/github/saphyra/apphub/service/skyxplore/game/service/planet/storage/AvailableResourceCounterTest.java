package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResources;
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
public class AvailableResourceCounterTest {
    private static final String DATA_ID_1 = "data-id-1";
    private static final String DATA_ID_2 = "data-id-2";
    private static final Integer STORED_AMOUNT = 21453;
    private static final Integer ALLOCATED_AMOUNT = 234;
    private static final UUID LOCATION = UUID.randomUUID();

    @InjectMocks
    private AvailableResourceCounter underTest;

    @Mock
    private StoredResource storedResource;

    @Mock
    private AllocatedResource allocatedResource1;

    @Mock
    private AllocatedResource allocatedResource2;

    @Mock
    private StoredResources storedResources;

    @Mock
    private AllocatedResources allocatedResources;

    @Mock
    private GameData gameData;

    @Test
    public void countAvailableAmount() {
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storedResources.findByLocationAndDataId(LOCATION, DATA_ID_1)).willReturn(Optional.of(storedResource));

        given(gameData.getAllocatedResources()).willReturn(allocatedResources);
        given(allocatedResources.getByLocationAndDataId(LOCATION, DATA_ID_1)).willReturn(List.of(allocatedResource1, allocatedResource2));

        given(storedResource.getAmount()).willReturn(STORED_AMOUNT);
        given(allocatedResource1.getAmount()).willReturn(ALLOCATED_AMOUNT);

        int result = underTest.countAvailableAmount(gameData, LOCATION, DATA_ID_1);

        assertThat(result).isEqualTo(STORED_AMOUNT - ALLOCATED_AMOUNT);
    }
}
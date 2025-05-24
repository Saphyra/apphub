package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order;

import com.github.saphyra.apphub.api.skyxplore.model.game.ReservedStorageModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResourceConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorageConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResourceFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorageFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductionRequirementsAllocationServiceTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer AMOUNT = 10;
    private static final Integer AVAILABLE_AMOUNT = 4;
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private AvailableResourceCounter availableResourceCounter;

    @Mock
    private AllocatedResourceFactory allocatedResourceFactory;

    @Mock
    private ReservedStorageFactory reservedStorageFactory;

    @Mock
    private AllocatedResourceConverter allocatedResourceConverter;

    @Mock
    private ReservedStorageConverter reservedStorageConverter;

    @InjectMocks
    private ProductionRequirementsAllocationService underTest;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private GameData gameData;

    @Mock
    private AllocatedResource allocatedResource;

    @Mock
    private ReservedStorage reservedStorage;

    @Mock
    private AllocatedResources allocatedResources;

    @Mock
    private ReservedStorages reservedStorages;

    @Mock
    private AllocatedResourceModel allocatedResourceModel;

    @Mock
    private ReservedStorageModel reservedStorageModel;

    @Test
    void allocate() {
        given(availableResourceCounter.countAvailableAmount(gameData, LOCATION, DATA_ID)).willReturn(AVAILABLE_AMOUNT);
        given(allocatedResourceFactory.create(LOCATION, EXTERNAL_REFERENCE, DATA_ID, AVAILABLE_AMOUNT)).willReturn(allocatedResource);
        given(reservedStorageFactory.create(LOCATION, EXTERNAL_REFERENCE, DATA_ID, AMOUNT - AVAILABLE_AMOUNT)).willReturn(reservedStorage);
        given(gameData.getAllocatedResources()).willReturn(allocatedResources);
        given(gameData.getReservedStorages()).willReturn(reservedStorages);
        given(reservedStorage.getReservedStorageId()).willReturn(RESERVED_STORAGE_ID);
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(allocatedResourceConverter.toModel(GAME_ID, allocatedResource)).willReturn(allocatedResourceModel);
        given(reservedStorageConverter.toModel(GAME_ID, reservedStorage)).willReturn(reservedStorageModel);

        UUID result = underTest.allocate(progressDiff, gameData, LOCATION, EXTERNAL_REFERENCE, DATA_ID, AMOUNT);

        assertThat(result).isEqualTo(RESERVED_STORAGE_ID);

        verify(allocatedResources).add(allocatedResource);
        verify(reservedStorages).add(reservedStorage);
        then(progressDiff).should().save(allocatedResourceModel);
        then(progressDiff).should().save(reservedStorageModel);
    }
}
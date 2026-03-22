package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage;

import com.github.saphyra.apphub.api.feature.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.feature.skyxplore.model.game.StoredResourceModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResourceConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResources;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AllocationRemovalServiceTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();

    @Mock
    private StoredResourceConverter storedResourceConverter;

    @InjectMocks
    private AllocationRemovalService underTest;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private GameData gameData;

    @Mock
    private StoredResources storedResources;

    @Mock
    private ReservedStorages reservedStorages;

    @Mock
    private ReservedStorage reservedStorage;

    @Mock
    private StoredResource storedResource;

    @Mock
    private StoredResourceModel storedResourceModel;

    @Test
    void removeAllocationsAndReservations() {
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(gameData.getReservedStorages()).willReturn(reservedStorages);
        given(storedResources.getByAllocatedBy(EXTERNAL_REFERENCE)).willReturn(List.of(storedResource));
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(storedResourceConverter.toModel(GAME_ID, storedResource)).willReturn(storedResourceModel);
        given(reservedStorage.getReservedStorageId()).willReturn(RESERVED_STORAGE_ID);
        given(reservedStorage.isExisting()).willReturn(true);
        given(reservedStorages.getByExternalReference(EXTERNAL_REFERENCE)).willReturn(List.of(reservedStorage));

        underTest.removeAllocationsAndReservations(progressDiff, gameData, EXTERNAL_REFERENCE);

        then(storedResource).should().setAllocatedBy(null);
        then(progressDiff).should().save(storedResourceModel);
        then(progressDiff).should().delete(RESERVED_STORAGE_ID, GameItemType.RESERVED_STORAGE, true);
        then(reservedStorages).should().remove(reservedStorage);
    }
}
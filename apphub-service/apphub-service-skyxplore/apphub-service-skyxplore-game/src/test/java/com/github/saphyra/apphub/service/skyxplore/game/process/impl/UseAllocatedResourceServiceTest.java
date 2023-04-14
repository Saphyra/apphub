package com.github.saphyra.apphub.service.skyxplore.game.process.impl;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResources;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UseAllocatedResourceServiceTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer AMOUNT = 234;
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();

    @InjectMocks
    private UseAllocatedResourceService underTest;

    @Mock
    private SyncCache syncCache;

    @Mock
    private GameData gameData;

    @Mock
    private AllocatedResources allocatedResources;

    @Mock
    private ReservedStorages reservedStorages;

    @Mock
    private StoredResources storedResources;

    @Mock
    private ReservedStorage reservedStorage;

    @Mock
    private AllocatedResource allocatedResource;

    @Mock
    private StoredResource storedResource;

    @Test
    public void resolveAllocations() {
        given(gameData.getReservedStorages()).willReturn(reservedStorages);
        given(reservedStorages.getByExternalReference(EXTERNAL_REFERENCE)).willReturn(List.of(reservedStorage));
        given(reservedStorage.getDataId()).willReturn(DATA_ID);

        given(gameData.getAllocatedResources()).willReturn(allocatedResources);
        given(allocatedResources.findByExternalReferenceAndDataIdValidated(EXTERNAL_REFERENCE, DATA_ID)).willReturn(allocatedResource);
        given(allocatedResource.getAmount()).willReturn(AMOUNT);

        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storedResources.findByLocationAndDataIdOrDefault(PLANET_ID, DATA_ID)).willReturn(storedResource);

        underTest.resolveAllocations(syncCache, gameData, PLANET_ID, USER_ID, EXTERNAL_REFERENCE);

        verify(storedResource).decreaseAmount(AMOUNT);
        verify(allocatedResource).setAmount(0);

        verify(syncCache).allocatedResourceResolved(USER_ID, PLANET_ID, allocatedResource, storedResource);
    }
}
package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSettings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResources;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class StorageDetailsLoaderTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private AllocatedResourceLoader allocatedResourceLoader;

    @Mock
    private ReservedStorageLoader reservedStorageLoader;

    @Mock
    private StoredResourceLoader storedResourceLoader;

    @Mock
    private StorageSettingLoader storageSettingLoader;

    @InjectMocks
    private StorageDetailsLoader underTest;

    @Mock
    private AllocatedResource allocatedResource;

    @Mock
    private ReservedStorages reservedStorages;

    @Mock
    private StorageSettings storageSettings;

    @Mock
    private StoredResources storedResources;

    @Test
    public void load() {
        given(allocatedResourceLoader.load(LOCATION)).willReturn(new AllocatedResources(Arrays.asList(allocatedResource)));
        given(reservedStorageLoader.load(LOCATION)).willReturn(reservedStorages);
        given(storedResourceLoader.load(GAME_ID, LOCATION)).willReturn(storedResources);
        given(storageSettingLoader.load(LOCATION)).willReturn(storageSettings);

        StorageDetails result = underTest.load(GAME_ID, LOCATION);

        assertThat(result.getAllocatedResources()).containsExactly(allocatedResource);
        assertThat(result.getReservedStorages()).isEqualTo(reservedStorages);
        assertThat(result.getStoredResources()).isEqualTo(storedResources);
        assertThat(result.getStorageSettings()).isEqualTo(storageSettings);
    }
}
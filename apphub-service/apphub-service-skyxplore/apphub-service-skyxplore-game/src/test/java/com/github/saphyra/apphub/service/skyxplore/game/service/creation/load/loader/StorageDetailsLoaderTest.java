package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSettings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class StorageDetailsLoaderTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String DATA_ID = "data-id";

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
    private StoredResource storedResource;

    @Mock
    private StorageSettings storageSettings;

    @Test
    public void load() {
        given(allocatedResourceLoader.load(LOCATION)).willReturn(new AllocatedResources(Arrays.asList(allocatedResource)));
        given(reservedStorageLoader.load(LOCATION)).willReturn(reservedStorages);
        given(storedResourceLoader.load(LOCATION)).willReturn(CollectionUtils.singleValueMap(DATA_ID, storedResource));
        given(storageSettingLoader.load(LOCATION)).willReturn(storageSettings);

        StorageDetails result = underTest.load(LOCATION);

        assertThat(result.getAllocatedResources()).containsExactly(allocatedResource);
        assertThat(result.getReservedStorages()).isEqualTo(reservedStorages);
        assertThat(result.getStoredResources()).containsEntry(DATA_ID, storedResource);
        assertThat(result.getStorageSettings()).isEqualTo(storageSettings);
    }
}
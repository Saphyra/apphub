package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllocatedResourceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.ReservedStorageModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.StorageSettingModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.StoredResourceModel;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class StorageDetailsToModelConverterTest {
    private static final UUID LOCATION = UUID.randomUUID();

    @Mock
    private AllocatedResourceToModelConverter allocatedResourceConverter;

    @Mock
    private ReservedStorageToModelConverter reservedStorageConverter;

    @Mock
    private StoredResourceToModelConverter storedResourceConverter;

    @Mock
    private StorageSettingToModelConverter storageSettingConverter;

    @InjectMocks
    private StorageDetailsToModelConverter underTest;

    @Mock
    private Game game;

    @Mock
    private AllocatedResource allocatedResource;

    @Mock
    private AllocatedResourceModel allocatedResourceModel;

    @Mock
    private ReservedStorage reservedStorage;

    @Mock
    private ReservedStorageModel reservedStorageModel;

    @Mock
    private StoredResource storedResource;

    @Mock
    private StoredResourceModel storedResourceModel;

    @Mock
    private StorageSetting storageSetting;

    @Mock
    private StorageSettingModel storageSettingModel;

    @Test
    public void convertDeep() {
        given(allocatedResourceConverter.convert(Arrays.asList(allocatedResource), game)).willReturn(Arrays.asList(allocatedResourceModel));
        given(reservedStorageConverter.convert(Arrays.asList(reservedStorage), game, LOCATION, LocationType.PLANET)).willReturn(Arrays.asList(reservedStorageModel));
        given(storedResourceConverter.convert(CollectionUtils.singleValueMap("", storedResource), game)).willReturn(Arrays.asList(storedResourceModel));
        given(storageSettingConverter.convert(Arrays.asList(storageSetting), game)).willReturn(Arrays.asList(storageSettingModel));

        StorageDetails storageDetails = new StorageDetails();
        storageDetails.getAllocatedResources().add(allocatedResource);
        storageDetails.getReservedStorages().add(reservedStorage);
        storageDetails.getStorageSettings().add(storageSetting);
        storageDetails.getStoredResources().put("", storedResource);

        List<GameItem> result = underTest.convertDeep(storageDetails, game, LOCATION, LocationType.PLANET);

        assertThat(result).containsExactlyInAnyOrder(allocatedResourceModel, reservedStorageModel, storageSettingModel, storedResourceModel);
    }
}
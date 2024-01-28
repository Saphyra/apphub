package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResources;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class StoredResourceAmountQueryServiceTest {
    private static final String DATA_ID_1 = "data-id-1";
    private static final String DATA_ID_2 = "data-id-2";
    private static final Integer AMOUNT = 245;
    private static final UUID LOCATION = UUID.randomUUID();
    @Mock
    private ResourceDataService resourceDataService;

    @InjectMocks
    private StoredResourceAmountQueryService underTest;

    @Mock
    private GameData gameData;

    @Mock
    private StoredResource storedResource1;

    @Mock
    private StoredResource storedResource2;

    @Mock
    private ResourceData resourceData;

    @Mock
    private StoredResources storedResources;

    @Test
    public void getActualAmount_byLocationAndDataId() {
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storedResources.findByLocationAndDataId(LOCATION, DATA_ID_1)).willReturn(Optional.of(storedResource1));

        given(storedResource1.getAmount()).willReturn(AMOUNT);

        int result = underTest.getActualAmount(gameData, LOCATION, DATA_ID_1);

        assertThat(result).isEqualTo(AMOUNT);
    }

    @Test
    public void getActualAmount_byLocationAndStorageType() {
        given(gameData.getStoredResources()).willReturn(storedResources);

        given(storedResources.getByLocation(LOCATION)).willReturn(List.of(storedResource1, storedResource2));

        given(storedResource1.getDataId()).willReturn(DATA_ID_1);
        given(storedResource2.getDataId()).willReturn(DATA_ID_2);

        given(resourceData.getId()).willReturn(DATA_ID_1);
        given(resourceDataService.getByStorageType(StorageType.BULK)).willReturn(Arrays.asList(resourceData));
        given(storedResource1.getAmount()).willReturn(AMOUNT);

        int result = underTest.getActualAmount(gameData, LOCATION, StorageType.BULK);

        assertThat(result).isEqualTo(AMOUNT);
    }

    @Test
    public void getActualStorageAmount() {
        given(gameData.getStoredResources()).willReturn(storedResources);

        given(storedResources.getByLocation(LOCATION)).willReturn(List.of(storedResource1, storedResource2));


        given(resourceDataService.getByStorageType(StorageType.BULK)).willReturn(Arrays.asList(resourceData));
        given(storedResource1.getDataId()).willReturn(DATA_ID_1);
        given(storedResource1.getAmount()).willReturn(AMOUNT);
        given(resourceData.getId()).willReturn(DATA_ID_1);

        int result = underTest.getActualAmount(gameData, LOCATION, StorageType.BULK);

        assertThat(result).isEqualTo(AMOUNT);
    }
}
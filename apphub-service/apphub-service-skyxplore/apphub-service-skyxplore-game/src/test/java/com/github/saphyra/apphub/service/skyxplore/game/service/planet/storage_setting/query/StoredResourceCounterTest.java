package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting.query;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class StoredResourceCounterTest {
    private static final String DATA_ID_1 = "data-id-1";
    private static final String DATA_ID_2 = "data-id-2";
    private static final Integer AMOUNT = 3214;

    @Mock
    private ResourceDataService resourceDataService;

    @InjectMocks
    private StoredResourceCounter underTest;

    @Mock
    private StoredResource storedResource1;

    @Mock
    private StoredResource storedResource2;

    @Mock
    private ResourceData resourceData1;

    @Mock
    private ResourceData resourceData2;

    @Test
    public void countStoredResources() {
        given(storedResource1.getDataId()).willReturn(DATA_ID_1);
        given(storedResource2.getDataId()).willReturn(DATA_ID_2);
        given(resourceDataService.get(DATA_ID_1)).willReturn(resourceData1);
        given(resourceDataService.get(DATA_ID_2)).willReturn(resourceData2);
        given(resourceData1.getStorageType()).willReturn(StorageType.CITIZEN);
        given(resourceData2.getStorageType()).willReturn(StorageType.BULK);
        given(storedResource1.getAmount()).willReturn(AMOUNT);

        int result = underTest.countStoredResources(StorageType.CITIZEN, CollectionUtils.toMap(new BiWrapper<>(DATA_ID_1, storedResource1), new BiWrapper<>(DATA_ID_2, storedResource2)));

        assertThat(result).isEqualTo(AMOUNT);
    }
}
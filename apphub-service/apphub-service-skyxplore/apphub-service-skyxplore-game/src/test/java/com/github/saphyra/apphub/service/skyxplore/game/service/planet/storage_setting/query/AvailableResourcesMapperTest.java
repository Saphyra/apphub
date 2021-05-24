package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting.query;

import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSetting;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AvailableResourcesMapperTest {
    private static final String DATA_ID_1 = "data-id-1";
    private static final String DATA_ID_2 = "data-id-2";

    @Mock
    private ResourceDataService resourceDataService;

    @InjectMocks
    private AvailableResourcesMapper underTest;

    @Mock
    private StorageSetting storageSetting;

    @Test
    public void getAvailableResources() {
        given(resourceDataService.keySet()).willReturn(CollectionUtils.toSet(DATA_ID_1, DATA_ID_2));
        given(storageSetting.getDataId()).willReturn(DATA_ID_1);

        List<String> result = underTest.getAvailableResources(Arrays.asList(storageSetting));

        assertThat(result).containsExactly(DATA_ID_2);
    }
}
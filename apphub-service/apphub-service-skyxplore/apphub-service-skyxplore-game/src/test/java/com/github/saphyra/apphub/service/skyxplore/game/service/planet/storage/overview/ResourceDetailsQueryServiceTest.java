package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.ResourceDetailsResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;

@ExtendWith(MockitoExtension.class)
public class ResourceDetailsQueryServiceTest {
    @Mock
    private ResourceDataService resourceDataService;

    @Mock
    private ResourceDetailsResponseMapper resourceDetailsResponseMapper;

    @InjectMocks
    private ResourceDetailsQueryService underTest;

    @Mock
    private Planet planet;

    @Mock
    private StorageDetails storageDetails;

    @Mock
    private ResourceData resourceData1;

    @Mock
    private ResourceData resourceData2;

    @Mock
    private ResourceDetailsResponse resourceDetailsResponse;

    @Mock
    private ResourceDetailsResponse emptyResourceDetailsResponse;

    @Test
    public void getResourceDetails() {
        given(planet.getStorageDetails()).willReturn(storageDetails);

        given(resourceDataService.getByStorageType(StorageType.BULK)).willReturn(Arrays.asList(resourceData1, resourceData2));
        given(resourceDetailsResponseMapper.createResourceData(resourceData1, storageDetails)).willReturn(emptyResourceDetailsResponse);
        given(resourceDetailsResponseMapper.createResourceData(resourceData2, storageDetails)).willReturn(resourceDetailsResponse);
        given(emptyResourceDetailsResponse.valuePresent()).willReturn(false);
        given(resourceDetailsResponse.valuePresent()).willReturn(true);

        List<ResourceDetailsResponse> result = underTest.getResourceDetails(planet, StorageType.BULK);

        assertThat(result).containsExactly(resourceDetailsResponse);
    }
}
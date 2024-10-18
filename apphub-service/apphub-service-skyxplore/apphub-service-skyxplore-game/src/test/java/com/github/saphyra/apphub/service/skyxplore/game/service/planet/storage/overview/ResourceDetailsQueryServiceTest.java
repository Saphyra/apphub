package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.ResourceDetailsResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ResourceDetailsQueryServiceTest {
    private static final UUID LOCATION = UUID.randomUUID();

    @Mock
    private ResourceDataService resourceDataService;

    @Mock
    private ResourceDetailsResponseMapper resourceDetailsResponseMapper;

    @InjectMocks
    private ResourceDetailsQueryService underTest;

    @Mock
    private GameData gameData;

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
        given(resourceDataService.getByStorageType(StorageType.CONTAINER)).willReturn(Arrays.asList(resourceData1, resourceData2));
        given(resourceDetailsResponseMapper.createResourceData(gameData, LOCATION, resourceData1)).willReturn(emptyResourceDetailsResponse);
        given(resourceDetailsResponseMapper.createResourceData(gameData, LOCATION, resourceData2)).willReturn(resourceDetailsResponse);
        given(emptyResourceDetailsResponse.valuePresent()).willReturn(false);
        given(resourceDetailsResponse.valuePresent()).willReturn(true);

        List<ResourceDetailsResponse> result = underTest.getResourceDetails(gameData, LOCATION, StorageType.CONTAINER);

        assertThat(result).containsExactly(resourceDetailsResponse);
    }
}
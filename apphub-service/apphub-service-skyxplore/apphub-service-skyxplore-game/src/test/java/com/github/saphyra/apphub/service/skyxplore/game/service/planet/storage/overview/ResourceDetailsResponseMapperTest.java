package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.ResourceDetailsResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.ActualResourceAmountQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.AllocatedResourceAmountQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.ReservedStorageQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ResourceDetailsResponseMapperTest {
    private static final String DATA_ID = "data-id";
    private static final Integer RESERVED_STORAGE_AMOUNT = 232;
    private static final Integer ACTUAL_AMOUNT = 2552;
    private static final Integer ALLOCATED_AMOUNT = 3563;
    private static final UUID LOCATION = UUID.randomUUID();

    @Mock
    private ReservedStorageQueryService reservedStorageQueryService;

    @Mock
    private ActualResourceAmountQueryService actualResourceAmountQueryService;

    @Mock
    private AllocatedResourceAmountQueryService allocatedResourceAmountQueryService;

    @InjectMocks
    private ResourceDetailsResponseMapper underTest;

    @Mock
    private ResourceData resourceData;

    @Mock
    private GameData gameData;

    @Test
    public void createResourceData() {
        given(resourceData.getId()).willReturn(DATA_ID);

        given(reservedStorageQueryService.getReservedAmount(gameData, LOCATION, DATA_ID)).willReturn(RESERVED_STORAGE_AMOUNT);
        given(actualResourceAmountQueryService.getActualAmount(gameData, LOCATION, DATA_ID)).willReturn(ACTUAL_AMOUNT);
        given(allocatedResourceAmountQueryService.getAllocatedResourceAmount(gameData, LOCATION, DATA_ID)).willReturn(ALLOCATED_AMOUNT);

        ResourceDetailsResponse result = underTest.createResourceData(gameData, LOCATION, resourceData);

        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getReservedStorageAmount()).isEqualTo(RESERVED_STORAGE_AMOUNT);
        assertThat(result.getActualAmount()).isEqualTo(ACTUAL_AMOUNT);
        assertThat(result.getAllocatedResourceAmount()).isEqualTo(ALLOCATED_AMOUNT);
    }
}
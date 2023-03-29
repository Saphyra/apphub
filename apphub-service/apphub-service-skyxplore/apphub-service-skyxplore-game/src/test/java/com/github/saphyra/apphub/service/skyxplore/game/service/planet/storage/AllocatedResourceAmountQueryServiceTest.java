package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResources;
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
public class AllocatedResourceAmountQueryServiceTest {
    private static final String DATA_ID_1 = "data-id-1";
    private static final String DATA_ID_2 = "data-id-2";
    private static final Integer AMOUNT = 234;
    private static final UUID LOCATION = UUID.randomUUID();

    @Mock
    private ResourceDataService resourceDataService;

    @InjectMocks
    private AllocatedResourceAmountQueryService underTest;

    @Mock
    private AllocatedResources allocatedResources;

    @Mock
    private GameData gameData;

    @Mock
    private AllocatedResource allocatedResource1;

    @Mock
    private AllocatedResource allocatedResource2;

    @Mock
    private ResourceData resourceData;

    @Test
    public void byLocationAndDataId() {
        given(gameData.getAllocatedResources()).willReturn(allocatedResources);
        given(allocatedResources.getByLocationAndDataId(LOCATION, DATA_ID_1)).willReturn(List.of(allocatedResource1));


        given(allocatedResource1.getAmount()).willReturn(AMOUNT);

        int result = underTest.getAllocatedResourceAmount(gameData, LOCATION, DATA_ID_1);

        assertThat(result).isEqualTo(AMOUNT);
    }

    @Test
    public void byLocationAndStorageType() {
        given(gameData.getAllocatedResources()).willReturn(allocatedResources);
        given(allocatedResources.getByLocation(LOCATION)).willReturn(List.of(allocatedResource1, allocatedResource2));

        given(resourceDataService.getByStorageType(StorageType.BULK)).willReturn(Arrays.asList(resourceData));
        given(resourceData.getId()).willReturn(DATA_ID_1);

        given(allocatedResource1.getDataId()).willReturn(DATA_ID_1);
        given(allocatedResource2.getDataId()).willReturn(DATA_ID_2);
        given(allocatedResource1.getAmount()).willReturn(AMOUNT);

        int result = underTest.getAllocatedResourceAmount(gameData, LOCATION, StorageType.BULK);

        assertThat(result).isEqualTo(AMOUNT);
    }
}
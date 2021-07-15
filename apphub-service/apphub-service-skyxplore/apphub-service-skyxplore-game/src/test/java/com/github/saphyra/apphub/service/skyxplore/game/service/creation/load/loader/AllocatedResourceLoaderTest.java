package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllocatedResourceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
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
public class AllocatedResourceLoaderTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID ALLOCATED_RESOURCE_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer AMOUNT = 42;

    @Mock
    private GameItemLoader gameItemLoader;

    @InjectMocks
    private AllocatedResourceLoader underTest;

    @Mock
    private AllocatedResourceModel allocatedResourceModel;

    @Test
    public void load() {
        given(gameItemLoader.loadChildren(LOCATION, GameItemType.ALLOCATED_RESOURCE, AllocatedResourceModel[].class)).willReturn(Arrays.asList(allocatedResourceModel));

        given(allocatedResourceModel.getId()).willReturn(ALLOCATED_RESOURCE_ID);
        given(allocatedResourceModel.getLocation()).willReturn(LOCATION);
        given(allocatedResourceModel.getLocationType()).willReturn(LocationType.PLANET.name());
        given(allocatedResourceModel.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(allocatedResourceModel.getDataId()).willReturn(DATA_ID);
        given(allocatedResourceModel.getAmount()).willReturn(AMOUNT);

        List<AllocatedResource> result = underTest.load(LOCATION);

        assertThat(result).hasSize(1);
        AllocatedResource allocatedResource = result.get(0);
        assertThat(allocatedResource.getAllocatedResourceId()).isEqualTo(ALLOCATED_RESOURCE_ID);
        assertThat(allocatedResource.getLocation()).isEqualTo(LOCATION);
        assertThat(allocatedResource.getLocationType()).isEqualTo(LocationType.PLANET);
        assertThat(allocatedResource.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(allocatedResource.getDataId()).isEqualTo(DATA_ID);
        assertThat(allocatedResource.getAmount()).isEqualTo(AMOUNT);
    }
}
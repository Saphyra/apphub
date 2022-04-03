package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.StoredResourceModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class StoredResourceLoaderTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID STORED_RESOURCE_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer AMOUNT = 354;
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private GameItemLoader gameItemLoader;

    @InjectMocks
    private StoredResourceLoader underTest;

    @Mock
    private StoredResourceModel storedResourceModel;

    @Test
    public void load() {
        given(gameItemLoader.loadChildren(LOCATION, GameItemType.STORED_RESOURCE, StoredResourceModel[].class)).willReturn(Arrays.asList(storedResourceModel));

        given(storedResourceModel.getId()).willReturn(STORED_RESOURCE_ID);
        given(storedResourceModel.getLocation()).willReturn(LOCATION);
        given(storedResourceModel.getLocationType()).willReturn(LocationType.PLANET.name());
        given(storedResourceModel.getDataId()).willReturn(DATA_ID);
        given(storedResourceModel.getAmount()).willReturn(AMOUNT);

        Map<String, StoredResource> result = underTest.load(GAME_ID, LOCATION);

        assertThat(result).hasSize(1);
        StoredResource storedResource = result.get(DATA_ID);
        assertThat(storedResource.getStoredResourceId()).isEqualTo(STORED_RESOURCE_ID);
        assertThat(storedResource.getLocation()).isEqualTo(LOCATION);
        assertThat(storedResource.getLocationType()).isEqualTo(LocationType.PLANET);
        assertThat(storedResource.getDataId()).isEqualTo(DATA_ID);
        assertThat(storedResource.getAmount()).isEqualTo(AMOUNT);
    }
}
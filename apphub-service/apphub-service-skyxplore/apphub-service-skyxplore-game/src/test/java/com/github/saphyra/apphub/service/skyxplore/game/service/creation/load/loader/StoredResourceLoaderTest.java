package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.StoredResourceModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResources;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.StoredResourcesFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StoredResourceLoaderTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID STORED_RESOURCE_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer AMOUNT = 354;
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private GameItemLoader gameItemLoader;

    @Mock
    private StoredResourcesFactory storedResourcesFactory;

    @InjectMocks
    private StoredResourceLoader underTest;

    @Mock
    private StoredResourceModel storedResourceModel;

    @Mock
    private StoredResources storedResources;

    @Test
    public void load() {
        given(storedResourcesFactory.create(GAME_ID, LOCATION, LocationType.PLANET)).willReturn(storedResources);
        given(gameItemLoader.loadChildren(LOCATION, GameItemType.STORED_RESOURCE, StoredResourceModel[].class)).willReturn(Arrays.asList(storedResourceModel));

        given(storedResourceModel.getId()).willReturn(STORED_RESOURCE_ID);
        given(storedResourceModel.getLocation()).willReturn(LOCATION);
        given(storedResourceModel.getLocationType()).willReturn(LocationType.PLANET.name());
        given(storedResourceModel.getDataId()).willReturn(DATA_ID);
        given(storedResourceModel.getAmount()).willReturn(AMOUNT);

        StoredResources result = underTest.load(GAME_ID, LOCATION);

        ArgumentCaptor<StoredResource> argumentCaptor = ArgumentCaptor.forClass(StoredResource.class);
        verify(storedResources).put(eq(DATA_ID), argumentCaptor.capture());

        StoredResource storedResource = argumentCaptor.getValue();

        assertThat(storedResource.getStoredResourceId()).isEqualTo(STORED_RESOURCE_ID);
        assertThat(storedResource.getLocation()).isEqualTo(LOCATION);
        assertThat(storedResource.getLocationType()).isEqualTo(LocationType.PLANET);
        assertThat(storedResource.getDataId()).isEqualTo(DATA_ID);
        assertThat(storedResource.getAmount()).isEqualTo(AMOUNT);
    }
}
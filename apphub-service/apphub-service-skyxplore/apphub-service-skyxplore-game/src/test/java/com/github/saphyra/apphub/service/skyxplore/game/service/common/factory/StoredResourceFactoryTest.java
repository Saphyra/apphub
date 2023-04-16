package com.github.saphyra.apphub.service.skyxplore.game.service.common.factory;

import com.github.saphyra.apphub.api.skyxplore.model.game.StoredResourceModel;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResourceConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResources;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StoredResourceFactoryTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final int AMOUNT = 21345;
    private static final UUID STORED_RESOURCE_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private StoredResourceConverter storedResourceConverter;

    @InjectMocks
    private StoredResourceFactory underTest;

    @Mock
    private GameData gameData;

    @Mock
    private SyncCache syncCache;

    @Mock
    private StoredResourceModel storedResourceModel;

    @Mock
    private StoredResources storedResources;

    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(STORED_RESOURCE_ID);
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(storedResourceConverter.toModel(eq(GAME_ID), any(StoredResource.class))).willReturn(storedResourceModel);

        StoredResource result = underTest.create(syncCache, gameData, LOCATION, DATA_ID, AMOUNT);

        assertThat(result.getStoredResourceId()).isEqualTo(STORED_RESOURCE_ID);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getAmount()).isEqualTo(AMOUNT);

        verify(storedResources).add(result);
        verify(syncCache).saveGameItem(storedResourceModel);
    }
}
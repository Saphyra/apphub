package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage;

import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.StoredResourceFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class StoredResourcesTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String DATA_ID = "data-id";

    @Mock
    private StoredResourceFactory storedResourceFactory;

    private StoredResources underTest;

    @Mock
    private StoredResource storedResource;

    @BeforeEach
    public void setUp() {
        underTest = StoredResources.builder()
            .storedResourceFactory(storedResourceFactory)
            .gameId(GAME_ID)
            .location(LOCATION)
            .locationType(LocationType.PRODUCTION)
            .build();
    }

    @Test
    public void get_alreadyExist() {
        underTest.put(DATA_ID, storedResource);

        StoredResource result = underTest.get(DATA_ID);

        assertThat(result).isEqualTo(storedResource);

        verifyNoInteractions(storedResourceFactory);
    }

    @Test
    public void get_create() {
        given(storedResourceFactory.create(LOCATION, LocationType.PRODUCTION, DATA_ID, 0)).willReturn(storedResource);
        given(storedResource.getDataId()).willReturn(DATA_ID);

        StoredResource result = underTest.get(DATA_ID);

        assertThat(result).isEqualTo(storedResource);
        assertThat(underTest).containsEntry(DATA_ID, storedResource);
    }
}
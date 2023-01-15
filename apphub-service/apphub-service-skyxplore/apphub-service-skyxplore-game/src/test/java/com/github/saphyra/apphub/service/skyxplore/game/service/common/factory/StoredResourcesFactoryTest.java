package com.github.saphyra.apphub.service.skyxplore.game.service.common.factory;

import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class StoredResourcesFactoryTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();

    @SuppressWarnings("unused")
    @Mock
    private StoredResourceFactory storedResourceFactory;

    @InjectMocks
    private StoredResourcesFactory underTest;

    @Test
    public void create() {
        assertThat(underTest.create(GAME_ID, LOCATION, LocationType.PRODUCTION)).isNotNull();
    }
}
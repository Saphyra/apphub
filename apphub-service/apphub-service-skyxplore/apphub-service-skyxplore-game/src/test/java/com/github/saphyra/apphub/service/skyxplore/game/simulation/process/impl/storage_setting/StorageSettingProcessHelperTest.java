package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.storage_setting;

import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.resource_request.ResourceRequestProcessFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class StorageSettingProcessHelperTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();

    @Mock
    private ResourceRequestProcessFactory resourceRequestProcessFactory;

    @InjectMocks
    private StorageSettingProcessHelper underTest;

    @Mock
    private Game game;

    @Test
    void orderResources() {
        underTest.orderResources(game, LOCATION, PROCESS_ID, RESERVED_STORAGE_ID);

        then(resourceRequestProcessFactory).should().save(game, LOCATION, PROCESS_ID, RESERVED_STORAGE_ID);
    }
}
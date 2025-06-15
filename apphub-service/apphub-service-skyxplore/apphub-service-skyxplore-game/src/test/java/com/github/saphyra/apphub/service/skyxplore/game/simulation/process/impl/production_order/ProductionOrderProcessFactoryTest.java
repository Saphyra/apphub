package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ProductionOrderProcessFactoryTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer MAX_BATCH_SIZE = 2;
    private static final Integer RESERVED_STORAGE_AMOUNT = 3;
    private static final UUID PROCESS_ID_1 = UUID.randomUUID();
    private static final UUID PROCESS_ID_2 = UUID.randomUUID();
    private static final UUID PRODUCER_BUILDING_MODULE_DATA_ID = UUID.randomUUID();
    private static final UUID ALLOCATED_RESOURCE_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private ResourceDataService resourceDataService;

    @Spy
    private final UuidConverter uuidConverter = new UuidConverter();

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private GameDao gameDao;

    @InjectMocks
    private ProductionOrderProcessFactory underTest;

    @Mock
    private GameData gameData;

    @Mock
    private ReservedStorages reservedStorages;

    @Mock
    private ReservedStorage reservedStorage;

    @Mock
    private ResourceData resourceData;

    @Mock
    private Game game;

    @Mock
    private ProcessModel model;

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.PRODUCTION_ORDER);
    }
}
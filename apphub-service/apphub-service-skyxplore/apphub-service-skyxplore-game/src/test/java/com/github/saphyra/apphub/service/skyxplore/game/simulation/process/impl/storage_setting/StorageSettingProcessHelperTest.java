package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.storage_setting;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ReservedStorageModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorageConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorageFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order.ProductionOrderProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order.ProductionOrderProcessFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StorageSettingProcessHelperTest {
    private static final int AMOUNT = 34;
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";

    @Mock
    private ProductionOrderProcessFactory productionOrderProcessFactory;

    @Mock
    private ReservedStorageFactory reservedStorageFactory;

    @Mock
    private ReservedStorageConverter reservedStorageConverter;

    @InjectMocks
    private StorageSettingProcessHelper underTest;

    @Mock
    private SyncCache syncCache;

    @Mock
    private GameData gameData;

    @Mock
    private StorageSetting storageSetting;

    @Mock
    private ReservedStorage reservedStorage;

    @Mock
    private ReservedStorages reservedStorages;

    @Mock
    private ReservedStorageModel reservedStorageModel;

    @Mock
    private ProductionOrderProcess productionOrderProcess;

    @Mock
    private Processes processes;

    @Mock
    private ProcessModel processModel;

    @Test
    void orderResources() {
        given(storageSetting.getLocation()).willReturn(LOCATION);
        given(storageSetting.getDataId()).willReturn(DATA_ID);
        given(reservedStorageFactory.create(LOCATION, PROCESS_ID, DATA_ID, AMOUNT)).willReturn(reservedStorage);
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(reservedStorageConverter.toModel(GAME_ID, reservedStorage)).willReturn(reservedStorageModel);
        given(productionOrderProcessFactory.create(gameData, PROCESS_ID, LOCATION, reservedStorage)).willReturn(List.of(productionOrderProcess));
        given(productionOrderProcess.toModel()).willReturn(processModel);
        given(gameData.getProcesses()).willReturn(processes);
        given(gameData.getReservedStorages()).willReturn(reservedStorages);

        underTest.orderResources(syncCache, gameData, PROCESS_ID, storageSetting, AMOUNT);

        verify(reservedStorages).add(reservedStorage);
        verify(syncCache).saveGameItem(reservedStorageModel);
        verify(processes).add(productionOrderProcess);
        verify(syncCache).saveGameItem(processModel);
    }
}
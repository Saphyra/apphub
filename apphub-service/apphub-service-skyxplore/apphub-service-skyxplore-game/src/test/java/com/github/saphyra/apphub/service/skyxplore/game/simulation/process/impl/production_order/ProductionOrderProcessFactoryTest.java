package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessParamKeys;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

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
    private static final String PRODUCER_BUILDING_DATA_ID = "producer-building-data-id";
    private static final UUID ALLOCATED_RESOURCE_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private ResourceDataService resourceDataService;

    @Spy
    private final UuidConverter uuidConverter = new UuidConverter();

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @InjectMocks
    private ProductionOrderProcessFactory underTest;

    @Mock
    private GameData gameData;

    @Mock
    private ReservedStorages reservedStorages;

    @Mock
    private ReservedStorage reservedStorage;

    @Mock
    private AllocatedResources allocatedResources;

    @Mock
    private AllocatedResource allocatedResource;

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

    @Test
    void create() {
        given(gameData.getReservedStorages()).willReturn(reservedStorages);
        given(reservedStorages.findByReservedStorageIdValidated(RESERVED_STORAGE_ID)).willReturn(reservedStorage);
        given(gameData.getAllocatedResources()).willReturn(allocatedResources);
        given(reservedStorage.getExternalReference()).willReturn(RESERVED_STORAGE_EXTERNAL_REFERENCE);
        given(reservedStorage.getDataId()).willReturn(DATA_ID);
        given(allocatedResources.findByExternalReferenceAndDataId(RESERVED_STORAGE_EXTERNAL_REFERENCE, DATA_ID)).willReturn(Optional.of(allocatedResource));
        given(resourceDataService.get(DATA_ID)).willReturn(resourceData);
        given(resourceData.getMaxProductionBatchSize()).willReturn(MAX_BATCH_SIZE);
        given(reservedStorage.getAmount()).willReturn(RESERVED_STORAGE_AMOUNT);
        given(idGenerator.randomUuid())
            .willReturn(PROCESS_ID_1)
            .willReturn(PROCESS_ID_2);
        given(reservedStorage.getReservedStorageId()).willReturn(RESERVED_STORAGE_ID);

        List<ProductionOrderProcess> result = underTest.create(gameData, EXTERNAL_REFERENCE, LOCATION, RESERVED_STORAGE_ID);

        assertThat(result).hasSize(2);

        Map<UUID, ProductionOrderProcess> map = result.stream()
            .collect(Collectors.toMap(ProductionOrderProcess::getProcessId, Function.identity()));

        assertThat(map.get(PROCESS_ID_1).getAmount()).isEqualTo(2);
        assertThat(map.get(PROCESS_ID_2).getAmount()).isEqualTo(1);
    }

    @Test
    void createFromModel() {
        given(game.getData()).willReturn(gameData);
        given(model.getId()).willReturn(PROCESS_ID_1);
        given(model.getStatus()).willReturn(ProcessStatus.DONE);
        Map<String, String> data = Map.of(
            ProcessParamKeys.PRODUCER_BUILDING_DATA_ID, PRODUCER_BUILDING_DATA_ID,
            ProcessParamKeys.ALLOCATED_RESOURCE_ID, uuidConverter.convertDomain(ALLOCATED_RESOURCE_ID),
            ProcessParamKeys.RESERVED_STORAGE_ID, uuidConverter.convertDomain(RESERVED_STORAGE_ID),
            ProcessParamKeys.AMOUNT, String.valueOf(RESERVED_STORAGE_AMOUNT)
        );
        given(model.getData()).willReturn(data);
        given(model.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(model.getLocation()).willReturn(LOCATION);

        ProductionOrderProcess result = underTest.createFromModel(game, model);

        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID_1);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.DONE);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.getAmount()).isEqualTo(RESERVED_STORAGE_AMOUNT);
    }
}
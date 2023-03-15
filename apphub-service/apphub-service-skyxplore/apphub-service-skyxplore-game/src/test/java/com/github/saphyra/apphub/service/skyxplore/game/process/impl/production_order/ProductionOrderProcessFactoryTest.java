package com.github.saphyra.apphub.service.skyxplore.game.process.impl.production_order;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.process.ProcessParamKeys;
import com.github.saphyra.apphub.test.common.ReflectionUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ProductionOrderProcessFactoryTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer MAX_PRODUCTION_BATCH_SIZE = 2;
    private static final Integer RESERVED_AMOUNT = 3;
    private static final UUID PROCESS_ID_1 = UUID.randomUUID();
    private static final UUID PROCESS_ID_2 = UUID.randomUUID();
    private static final String ALLOCATED_RESOURCE_ID_STRING = "allocated-resource-id";
    private static final String RESERVED_STORAGE_ID_STRING = "reserved-storage-id";
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID ALLOCATED_RESOURCE_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private ResourceDataService resourceDataService;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @InjectMocks
    private ProductionOrderProcessFactory underTest;

    @Mock
    private Game game;

    @Mock
    private Planet planet;

    @Mock
    private StorageDetails storageDetails;

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
    private Universe universe;

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.PRODUCTION_ORDER);
    }

    @Test
    public void create() throws NoSuchFieldException, IllegalAccessException {
        given(planet.getStorageDetails()).willReturn(storageDetails);
        given(storageDetails.getReservedStorages()).willReturn(reservedStorages);
        given(reservedStorages.findById(RESERVED_STORAGE_ID)).willReturn(Optional.of(reservedStorage));
        given(reservedStorage.getExternalReference()).willReturn(RESERVED_STORAGE_EXTERNAL_REFERENCE);
        given(reservedStorage.getDataId()).willReturn(DATA_ID);
        given(storageDetails.getAllocatedResources()).willReturn(allocatedResources);
        given(reservedStorage.getAmount()).willReturn(RESERVED_AMOUNT);
        given(resourceDataService.get(DATA_ID)).willReturn(resourceData);
        given(resourceData.getMaxProductionBatchSize()).willReturn(MAX_PRODUCTION_BATCH_SIZE);

        given(idGenerator.randomUuid())
            .willReturn(PROCESS_ID_1)
            .willReturn(PROCESS_ID_2);

        List<ProductionOrderProcess> result = underTest.create(EXTERNAL_REFERENCE, game, planet, RESERVED_STORAGE_ID);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getProcessId()).isEqualTo(PROCESS_ID_1);
        assertThat(result.get(0).getStatus()).isEqualTo(ProcessStatus.CREATED);
        assertThat((Integer) ReflectionUtils.getFieldValue(result.get(0), "amount")).isEqualTo(2);

        assertThat(result.get(1).getProcessId()).isEqualTo(PROCESS_ID_2);
        assertThat(result.get(1).getStatus()).isEqualTo(ProcessStatus.CREATED);
        assertThat((Integer) ReflectionUtils.getFieldValue(result.get(1), "amount")).isEqualTo(1);
    }

    @Test
    public void createFromModel() throws NoSuchFieldException, IllegalAccessException {
        ProcessModel model = new ProcessModel();
        model.setId(PROCESS_ID_1);
        model.setStatus(ProcessStatus.IN_PROGRESS);
        model.setExternalReference(EXTERNAL_REFERENCE);
        model.setLocation(PLANET_ID);
        model.setData(CollectionUtils.toMap(
            new BiWrapper<>(ProcessParamKeys.PRODUCER_BUILDING_DATA_ID, DATA_ID),
            new BiWrapper<>(ProcessParamKeys.AMOUNT, String.valueOf(RESERVED_AMOUNT)),
            new BiWrapper<>(ProcessParamKeys.RESERVED_STORAGE_ID, RESERVED_STORAGE_ID_STRING),
            new BiWrapper<>(ProcessParamKeys.ALLOCATED_RESOURCE_ID, ALLOCATED_RESOURCE_ID_STRING)
        ));

        given(game.getUniverse()).willReturn(universe);
        given(universe.findPlanetByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getStorageDetails()).willReturn(storageDetails);
        given(uuidConverter.convertEntity(RESERVED_STORAGE_ID_STRING)).willReturn(RESERVED_STORAGE_ID);
        given(uuidConverter.convertEntity(ALLOCATED_RESOURCE_ID_STRING)).willReturn(ALLOCATED_RESOURCE_ID);
        given(storageDetails.getReservedStorages()).willReturn(reservedStorages);
        given(storageDetails.getAllocatedResources()).willReturn(allocatedResources);
        given(reservedStorages.findByIdValidated(RESERVED_STORAGE_ID)).willReturn(reservedStorage);
        given(allocatedResources.findById(ALLOCATED_RESOURCE_ID)).willReturn(Optional.of(allocatedResource));

        ProductionOrderProcess result = underTest.createFromModel(game, model);

        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID_1);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);
        assertThat((String) ReflectionUtils.getFieldValue(result, "producerBuildingDataId")).isEqualTo(DATA_ID);
        assertThat((UUID) ReflectionUtils.getFieldValue(result, "externalReference")).isEqualTo(EXTERNAL_REFERENCE);
        assertThat((Game) ReflectionUtils.getFieldValue(result, "game")).isEqualTo(game);
        assertThat((Planet) ReflectionUtils.getFieldValue(result, "planet")).isEqualTo(planet);
        assertThat((AllocatedResource) ReflectionUtils.getFieldValue(result, "allocatedResource")).isEqualTo(allocatedResource);
        assertThat((ReservedStorage) ReflectionUtils.getFieldValue(result, "reservedStorage")).isEqualTo(reservedStorage);
        assertThat((Integer) ReflectionUtils.getFieldValue(result, "amount")).isEqualTo(RESERVED_AMOUNT);
        assertThat((ApplicationContextProxy) ReflectionUtils.getFieldValue(result, "applicationContextProxy")).isEqualTo(applicationContextProxy);
    }
}
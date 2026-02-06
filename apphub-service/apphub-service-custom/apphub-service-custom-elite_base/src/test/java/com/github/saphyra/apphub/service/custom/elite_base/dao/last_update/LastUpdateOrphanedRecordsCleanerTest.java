package com.github.saphyra.apphub.service.custom.elite_base.dao.last_update;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.common.BufferSynchronizationService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.equipment.Equipment;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.equipment.EquipmentDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.spaceship.Spaceship;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.spaceship.SpaceshipDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.commodity.Commodity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.commodity.CommodityDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.fc_material.FcMaterial;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.fc_material.FcMaterialDao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class LastUpdateOrphanedRecordsCleanerTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String ITEM_NAME = "item-name";

    @Autowired
    private CommodityDao commodityDao;

    @Autowired
    private FcMaterialDao fcMaterialDao;

    @Autowired
    private EquipmentDao equipmentDao;

    @Autowired
    private SpaceshipDao spaceshipDao;

    @Autowired
    private LastUpdateDao lastUpdateDao;

    @Autowired
    private LastUpdateOrphanedRecordsCleaner underTest;

    @Autowired
    private BufferSynchronizationService bufferSynchronizationService;

    @Autowired
    private List<CrudRepository<?, ?>> repositories;

    @MockitoBean
    private ErrorReporterService errorReporterService;

    @AfterEach
    void clear() {
        repositories.forEach(CrudRepository::deleteAll);
    }

    @Test
    void cleanup() {
        Commodity commodity = Commodity.builder()
            .externalReference(EXTERNAL_REFERENCE)
            .itemName(ITEM_NAME)
            .marketId(0L)
            .build();
        commodityDao.save(commodity);
        FcMaterial fcMaterial = FcMaterial.builder()
            .externalReference(EXTERNAL_REFERENCE)
            .itemName(ITEM_NAME)
            .marketId(0L)
            .build();
        fcMaterialDao.save(fcMaterial);

        Equipment equipment = Equipment.builder()
            .externalReference(EXTERNAL_REFERENCE)
            .itemName(ITEM_NAME)
            .marketId(0L)
            .build();
        equipmentDao.save(equipment);
        Spaceship spaceship = Spaceship.builder()
            .externalReference(EXTERNAL_REFERENCE)
            .itemName(ITEM_NAME)
            .marketId(0L)
            .build();
        spaceshipDao.save(spaceship);

        LastUpdate commodityLastUpdate = LastUpdate.builder()
            .externalReference(EXTERNAL_REFERENCE)
            .type(ItemType.COMMODITY)
            .build();
        lastUpdateDao.save(commodityLastUpdate);
        LastUpdate fcMaterialLastUpdate = LastUpdate.builder()
            .externalReference(EXTERNAL_REFERENCE)
            .type(ItemType.FC_MATERIAL)
            .build();
        lastUpdateDao.save(fcMaterialLastUpdate);
        LastUpdate equipmentLastUpdate = LastUpdate.builder()
            .externalReference(EXTERNAL_REFERENCE)
            .type(ItemType.EQUIPMENT)
            .build();
        lastUpdateDao.save(equipmentLastUpdate);
        LastUpdate spaceshipLastUpdate = LastUpdate.builder()
            .externalReference(EXTERNAL_REFERENCE)
            .type(ItemType.SPACESHIP)
            .build();
        lastUpdateDao.save(spaceshipLastUpdate);

        LastUpdate orphanedRecord = LastUpdate.builder()
            .externalReference(UUID.randomUUID())
            .type(ItemType.SPACESHIP)
            .build();
        lastUpdateDao.save(orphanedRecord);
        bufferSynchronizationService.synchronizeAll();

        assertThat(underTest.cleanupOrphanedRecords()).isEqualTo(1);

        assertThat(lastUpdateDao.findAll()).containsExactlyInAnyOrder(fcMaterialLastUpdate, spaceshipLastUpdate, equipmentLastUpdate, commodityLastUpdate);
    }
}
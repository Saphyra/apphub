package com.github.saphyra.apphub.service.custom.elite_base.dao.last_update;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.common.BufferSynchronizationService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.Commodity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.loadout.Loadout;
import com.github.saphyra.apphub.service.custom.elite_base.dao.loadout.LoadoutDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.loadout.LoadoutType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class LastUpdateOrphanedRecordsCleanerTest {
    private static final UUID COMMODITY_EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID LOADOUT_EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String COMMODITY_NAME = "commodity-name";
    private static final String LOADOUT_NAME = "loadout-name";

    @Autowired
    private CommodityDao commodityDao;

    @Autowired
    private LoadoutDao loadoutDao;

    @Autowired
    private LastUpdateDao lastUpdateDao;

    @Autowired
    private LastUpdateOrphanedRecordsCleaner underTest;

    @Autowired
    private BufferSynchronizationService bufferSynchronizationService;

    @Autowired
    private List<CrudRepository<?, ?>> repositories;

    @MockBean
    private ErrorReporterService errorReporterService;

    @AfterEach
    void clear() {
        repositories.forEach(CrudRepository::deleteAll);
    }

    @Test
    void cleanup(){
        Commodity commodity = Commodity.builder()
            .externalReference(COMMODITY_EXTERNAL_REFERENCE)
            .commodityName(COMMODITY_NAME)
            .build();
        commodityDao.save(commodity);
        Loadout loadout = Loadout.builder()
            .externalReference(LOADOUT_EXTERNAL_REFERENCE)
            .type(LoadoutType.OUTFITTING)
            .name(LOADOUT_NAME)
            .build();
        loadoutDao.save(loadout);
        LastUpdate commodityLastUpdate = LastUpdate.builder()
            .externalReference(COMMODITY_EXTERNAL_REFERENCE)
            .type(EntityType.COMMODITY)
            .build();
        lastUpdateDao.save(commodityLastUpdate);
        LastUpdate loadoutLastUpdate = LastUpdate.builder()
            .externalReference(LOADOUT_EXTERNAL_REFERENCE)
            .type(EntityType.SHIP_MODULE)
            .build();
        lastUpdateDao.save(loadoutLastUpdate);
        LastUpdate orphanedRecord = LastUpdate.builder()
            .externalReference(UUID.randomUUID())
            .type(EntityType.SHIP)
            .build();
        lastUpdateDao.save(orphanedRecord);
        bufferSynchronizationService.synchronizeAll();

        assertThat(underTest.cleanupOrphanedRecords()).isEqualTo(1);

        assertThat(lastUpdateDao.findAll()).containsExactlyInAnyOrder(commodityLastUpdate, loadoutLastUpdate);
    }
}
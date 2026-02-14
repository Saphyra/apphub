package com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.fc_material;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.common.BufferSynchronizationService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.Orphanage;
import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrier;
import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrierDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.Station;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.StationDao;
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
class FcMaterialOrphanedRecordCleanerTest {
    private static final UUID FLEET_CARRIER_ID = UUID.randomUUID();
    private static final UUID STATION_ID = UUID.randomUUID();
    private static final String ITEM_NAME = "item-name";
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final Long MARKET_ID = 324L;

    @Autowired
    private FcMaterialDao commodityDao;

    @Autowired
    private StationDao stationDao;

    @Autowired
    private FleetCarrierDao fleetCarrierDao;

    @Autowired
    private List<CrudRepository<?, ?>> repositories;

    @Autowired
    private BufferSynchronizationService bufferSynchronizationService;

    @MockitoBean
    private ErrorReporterService errorReporterService;

    @Autowired
    private FcMaterialOrphanedRecordCleaner underTest;

    @AfterEach
    void clear() {
        repositories.forEach(CrudRepository::deleteAll);
    }

    @Test
    void getOrphanage() {
        assertThat(underTest.getOrphanage()).isEqualTo(Orphanage.ITEM_FC_MATERIAL);
    }

    @Test
    void getPreconditions() {
        assertThat(underTest.getPreconditions()).containsExactlyInAnyOrder(Orphanage.STATION, Orphanage.FLEET_CARRIER);
    }

    @Test
    void cleanup() {
        FleetCarrier fleetCarrier = FleetCarrier.builder()
            .id(FLEET_CARRIER_ID)
            .starSystemId(STAR_SYSTEM_ID)
            .build();
        fleetCarrierDao.save(fleetCarrier);
        Station station = Station.builder()
            .id(STATION_ID)
            .starSystemId(STAR_SYSTEM_ID)
            .build();
        stationDao.save(station);

        FcMaterial stationCommodity = FcMaterial.builder()
            .externalReference(STATION_ID)
            .itemName(ITEM_NAME)
            .marketId(MARKET_ID)
            .build();
        commodityDao.save(stationCommodity);
        FcMaterial fleetCarrierCommodity = FcMaterial.builder()
            .externalReference(FLEET_CARRIER_ID)
            .itemName(ITEM_NAME)
            .marketId(MARKET_ID)
            .build();
        commodityDao.save(fleetCarrierCommodity);
        FcMaterial orphanedCommodity = FcMaterial.builder()
            .externalReference(UUID.randomUUID())
            .itemName(ITEM_NAME)
            .marketId(MARKET_ID)
            .build();
        commodityDao.save(orphanedCommodity);

        bufferSynchronizationService.synchronizeAll();

        assertThat(underTest.cleanupOrphanedRecords()).isEqualTo(1);

        assertThat(commodityDao.findAll()).containsExactlyInAnyOrder(stationCommodity, fleetCarrierCommodity);
    }
}
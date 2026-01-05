package com.github.saphyra.apphub.service.custom.elite_base.dao.commodity;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.common.BufferSynchronizationService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrier;
import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrierDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.Station;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.StationDao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class CommodityOrphanedRecordCleanerTest {
    private static final UUID STATION_ID = UUID.randomUUID();
    private static final UUID FLEET_CARRIER_ID = UUID.randomUUID();
    private static final String COMMODITY_NAME = "commodity-name";

    @Autowired
    private StationDao stationDao;

    @Autowired
    private FleetCarrierDao fleetCarrierDao;

    @Autowired
    private CommodityDao commodityDao;

    @Autowired
    private BufferSynchronizationService bufferSynchronizationService;

    @Autowired
    private CommodityOrphanedRecordCleaner underTest;

    @Autowired
    private List<CrudRepository<?, ?>> repositories;

    @MockBean
    private ErrorReporterService errorReporterService;

    @AfterEach
    void clear() {
        repositories.forEach(CrudRepository::deleteAll);
    }

    @Test
    void cleanup() {
        Station station = Station.builder()
            .id(STATION_ID)
            .starSystemId(UUID.randomUUID())
            .build();
        stationDao.save(station);
        FleetCarrier fleetCarrier = FleetCarrier.builder()
            .id(FLEET_CARRIER_ID)
            .starSystemId(UUID.randomUUID())
            .build();
        fleetCarrierDao.save(fleetCarrier);
        Commodity stationCommodity = Commodity.builder()
            .externalReference(STATION_ID)
            .commodityName(COMMODITY_NAME)
            .type(CommodityType.COMMODITY)
            .build();
        commodityDao.save(stationCommodity);
        Commodity fleetCarrierCommodity = Commodity.builder()
            .externalReference(FLEET_CARRIER_ID)
            .commodityName(COMMODITY_NAME)
            .type(CommodityType.COMMODITY)
            .build();
        commodityDao.save(fleetCarrierCommodity);
        Commodity orphanedRecord = Commodity.builder()
            .externalReference(UUID.randomUUID())
            .commodityName(COMMODITY_NAME)
            .type(CommodityType.COMMODITY)
            .build();
        commodityDao.save(orphanedRecord);
        bufferSynchronizationService.synchronizeAll();

        assertThat(underTest.cleanupOrphanedRecords()).isEqualTo(1);

        assertThat(commodityDao.findAll()).containsExactlyInAnyOrder(stationCommodity, fleetCarrierCommodity);
    }
}
package com.github.saphyra.apphub.service.custom.elite_base.dao.loadout;

import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
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
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class LoadoutOrphanedRecordCleanerTest {
    private static final UUID FLEET_CARRIER_ID = UUID.randomUUID();
    private static final Long FLEET_CARRIER_MARKET_ID = 1234567890L;
    private static final UUID STATION_ID = UUID.randomUUID();
    private static final Long STATION_MARKET_ID = 1234567891L;
    private static final String LOADOUT_NAME = "loadout-name";

    @Autowired
    private LoadoutDao loadoutDao;

    @Autowired
    private StationDao stationDao;

    @Autowired
    private FleetCarrierDao fleetCarrierDao;

    @Autowired
    private LoadoutOrphanedRecordCleaner underTest;

    @Autowired
    private List<CrudRepository<?, ?>> repositories;

    @Autowired
    private BufferSynchronizationService bufferSynchronizationService;

    @AfterEach
    void clear() {
        repositories.forEach(CrudRepository::deleteAll);
    }

    @Test
    void cleanup() {
        FleetCarrier fleetCarrier = FleetCarrier.builder()
            .id(FLEET_CARRIER_ID)
            .marketId(FLEET_CARRIER_MARKET_ID)
            .build();
        fleetCarrierDao.save(fleetCarrier);
        Station station = Station.builder()
            .id(STATION_ID)
            .marketId(STATION_MARKET_ID)
            .services(LazyLoadedField.loaded(List.of()))
            .economies(LazyLoadedField.loaded(List.of()))
            .build();
        stationDao.save(station);
        Loadout fcLoadout = Loadout.builder()
            .externalReference(FLEET_CARRIER_ID)
            .name(LOADOUT_NAME)
            .marketId(FLEET_CARRIER_MARKET_ID)
            .type(LoadoutType.OUTFITTING)
            .build();
        loadoutDao.save(fcLoadout);
        Loadout stationLoadout = Loadout.builder()
            .externalReference(STATION_ID)
            .name(LOADOUT_NAME)
            .marketId(STATION_MARKET_ID)
            .type(LoadoutType.OUTFITTING)
            .build();
        loadoutDao.save(stationLoadout);
        Loadout orphanedLoadout = Loadout.builder()
            .externalReference(UUID.randomUUID())
            .name(LOADOUT_NAME)
            .marketId(1234567892L)
            .type(LoadoutType.OUTFITTING)
            .build();
        loadoutDao.save(orphanedLoadout);
        bufferSynchronizationService.synchronizeAll();

        underTest.doCleanup();

        assertThat(loadoutDao.findAll()).containsExactlyInAnyOrder(fcLoadout, stationLoadout);
    }
}
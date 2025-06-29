package com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_economy;

import com.github.saphyra.apphub.service.custom.elite_base.dao.EconomyEnum;
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
class StationEconomyOrphanedRecordCleanerTest {
    private static final UUID STATION_ID = UUID.randomUUID();

    @Autowired
    private StationEconomyDao stationEconomyDao;

    @Autowired
    private StationDao stationDao;

    @Autowired
    private StationEconomyOrphanedRecordCleaner underTest;

    @Autowired
    private List<CrudRepository<?, ?>> repositories;

    @AfterEach
    void clear() {
        repositories.forEach(CrudRepository::deleteAll);
    }

    @Test
    void cleanup() {
        Station station = Station.builder()
            .id(STATION_ID)
            .build();
        stationDao.save(station);

        StationEconomy stationEconomy = StationEconomy.builder()
            .stationId(STATION_ID)
            .economy(EconomyEnum.AGRICULTURE)
            .build();
        stationEconomyDao.save(stationEconomy);

        StationEconomy orphanedStationEconomy = StationEconomy.builder()
            .stationId(UUID.randomUUID())
            .economy(EconomyEnum.AGRICULTURE)
            .build();
        stationEconomyDao.save(orphanedStationEconomy);

        underTest.cleanupOrphanedRecords();

        assertThat(stationEconomyDao.findAll()).containsExactly(stationEconomy);
    }
}
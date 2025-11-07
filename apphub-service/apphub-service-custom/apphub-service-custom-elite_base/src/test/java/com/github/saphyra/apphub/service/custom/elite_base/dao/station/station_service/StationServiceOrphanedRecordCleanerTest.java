package com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_service;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
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
class StationServiceOrphanedRecordCleanerTest {
    private static final UUID STATION_ID = UUID.randomUUID();

    @Autowired
    private StationServiceDao stationServiceDao;

    @Autowired
    private StationDao stationDao;

    @Autowired
    private StationServiceOrphanedRecordCleaner underTest;

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
        StationService stationService = StationService.builder()
            .stationId(STATION_ID)
            .service(StationServiceEnum.BARTENDER)
            .build();
        stationServiceDao.save(stationService);
        StationService orphanedRecord = StationService.builder()
            .stationId(UUID.randomUUID())
            .service(StationServiceEnum.MATERIAL_TRADER)
            .build();
        stationServiceDao.save(orphanedRecord);

        assertThat(underTest.cleanupOrphanedRecords()).isEqualTo(1);

        assertThat(stationServiceDao.findAll()).containsExactly(stationService);
    }
}
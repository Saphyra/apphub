package com.github.saphyra.apphub.service.custom.elite_base.dao.station.material_trader_override;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.Station;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.StationDao;
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
class MaterialTraderOverrideOrphanedRecordCleanerTest {
    private static final UUID STATION_ID = UUID.randomUUID();

    @Autowired
    private StationDao stationDao;

    @Autowired
    private MaterialTraderOverrideDao materialTraderOverrideDao;

    @Autowired
    private MaterialTraderOverrideOrphanedRecordCleaner underTest;

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
        MaterialTraderOverride materialTraderOverride = MaterialTraderOverride.builder()
            .stationId(STATION_ID)
            .build();
        materialTraderOverrideDao.save(materialTraderOverride);
        MaterialTraderOverride orphanedRecord = MaterialTraderOverride.builder()
            .stationId(UUID.randomUUID())
            .build();
        materialTraderOverrideDao.save(orphanedRecord);

        assertThat(underTest.cleanupOrphanedRecords()).isEqualTo(1);

        assertThat(materialTraderOverrideDao.findAll()).containsExactly(materialTraderOverride);
    }
}
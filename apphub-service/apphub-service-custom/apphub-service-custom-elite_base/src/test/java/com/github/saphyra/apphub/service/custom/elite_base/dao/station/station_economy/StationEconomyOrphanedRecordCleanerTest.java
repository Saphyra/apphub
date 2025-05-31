package com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_economy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

@ExtendWith(MockitoExtension.class)
class StationEconomyOrphanedRecordCleanerTest {
    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private StationEconomyOrphanedRecordCleaner underTest;

    @Test
    void doCleanup(){
        underTest.doCleanup();
    }
}
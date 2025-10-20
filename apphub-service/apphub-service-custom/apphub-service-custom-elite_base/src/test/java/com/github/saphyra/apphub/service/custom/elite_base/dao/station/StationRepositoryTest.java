package com.github.saphyra.apphub.service.custom.elite_base.dao.station;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@Slf4j
class StationRepositoryTest {
    private static final String ID_1 = "id-1";
    private static final String STAR_SYSTEM_ID_1 = "star-system-id-1";
    private static final String STATION_NAME_1 = "station-name-1";
    private static final Long MARKET_ID = 3423L;

    @Autowired
    private StationRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    void findByStarSystemIdAndStationName() {
        StationEntity entity = StationEntity.builder()
            .id(ID_1)
            .starSystemId(STAR_SYSTEM_ID_1)
            .stationName(STATION_NAME_1)
            .build();
        underTest.save(entity);

        assertThat(underTest.findByStarSystemIdAndStationName(STAR_SYSTEM_ID_1, STATION_NAME_1)).contains(entity);
    }

    @Test
    void findByMarketId() {
        StationEntity entity = StationEntity.builder()
            .id(ID_1)
            .marketId(MARKET_ID)
            .starSystemId(STAR_SYSTEM_ID_1)
            .build();
        underTest.save(entity);

        assertThat(underTest.findByMarketId(MARKET_ID)).contains(entity);
    }
}
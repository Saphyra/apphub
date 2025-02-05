package com.github.saphyra.apphub.service.elite_base.message_processing.dao.station.station_economy;

import com.github.saphyra.apphub.service.elite_base.message_processing.dao.EconomyEnum;
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
class StationEconomyRepositoryTest {
    private static final String STATION_ID_1 = "station-id-1";
    private static final String STATION_ID_2 = "station-id-2";

    @Autowired
    private StationEconomyRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    void getByStationId() {
        StationEconomyEntity entity1 = StationEconomyEntity.builder()
            .id(StationEconomyEntityId.builder()
                .stationId(STATION_ID_1)
                .economy(EconomyEnum.AGRICULTURE)
                .build())
            .build();
        underTest.save(entity1);
        StationEconomyEntity entity2 = StationEconomyEntity.builder()
            .id(StationEconomyEntityId.builder()
                .stationId(STATION_ID_2)
                .economy(EconomyEnum.AGRICULTURE)
                .build())
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByIdStationId(STATION_ID_1)).containsExactly(entity1);
    }
}
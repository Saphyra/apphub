package com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_service;

import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_service.StationServiceEntity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_service.StationServiceEnum;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_service.StationServiceRepository;
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
class StationServiceRepositoryTest {
    private static final String STATION_ID_1 = "station-id-1";
    private static final String STATION_ID_2 = "station-id-2";

    @Autowired
    private StationServiceRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    void getByStationId() {
        StationServiceEntity entity1 = StationServiceEntity.builder()
            .stationId(STATION_ID_1)
            .service(StationServiceEnum.BARTENDER)
            .build();
        underTest.save(entity1);
        StationServiceEntity entity2 = StationServiceEntity.builder()
            .stationId(STATION_ID_2)
            .service(StationServiceEnum.BARTENDER)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByStationId(STATION_ID_1)).containsExactly(entity1);
    }
}
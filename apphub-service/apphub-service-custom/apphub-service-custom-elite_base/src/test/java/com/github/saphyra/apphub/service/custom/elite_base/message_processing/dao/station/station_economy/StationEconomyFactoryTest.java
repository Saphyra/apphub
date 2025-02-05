package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.station.station_economy;

import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.EconomyEnum;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.Economy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class StationEconomyFactoryTest {
    private static final Double PROPORTION = 34.3;
    private static final UUID STATION_ID = UUID.randomUUID();

    @InjectMocks
    private StationEconomyFactory underTest;

    @Test
    void create() {
        assertThat(underTest.create(STATION_ID, new Economy("Industrial", PROPORTION)))
            .returns(STATION_ID, StationEconomy::getStationId)
            .returns(EconomyEnum.INDUSTRIAL, StationEconomy::getEconomy)
            .returns(PROPORTION, StationEconomy::getProportion);
    }
}
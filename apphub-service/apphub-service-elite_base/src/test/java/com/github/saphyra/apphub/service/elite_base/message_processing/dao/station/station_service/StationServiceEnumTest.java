package com.github.saphyra.apphub.service.elite_base.message_processing.dao.station.station_service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class StationServiceEnumTest {
    @Test
    void parse_null() {
        assertThat(StationServiceEnum.parse(null)).isNull();
    }

    @Test
    void parse_error() {
        assertThat(catchThrowable(() -> StationServiceEnum.parse("asd"))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void parse() {
        assertThat(StationServiceEnum.parse("carriermanagement")).isEqualTo(StationServiceEnum.CARRIER_MANAGEMENT);
    }
}
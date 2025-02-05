package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.fleet_carrier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

@ExtendWith(MockitoExtension.class)
class FleetCarrierDockingAccessTest {
    @Test
    void parse_null() {
        assertThat(FleetCarrierDockingAccess.parse(null)).isNull();
    }

    @Test
    void parse_error() {
        assertThat(catchThrowable(() -> FleetCarrierDockingAccess.parse("asd"))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void parse() {
        assertThat(FleetCarrierDockingAccess.parse("all")).isEqualTo(FleetCarrierDockingAccess.ALL);
    }
}
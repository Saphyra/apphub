package com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier;

import com.github.saphyra.apphub.service.custom.elite_base.dao.StationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class FleetCarrierTest {

    public static final String CARRIER_ID = "carrier-id";
    public static final String CARRIER_NAME = "carrier-name";

    @Test
    void getType() {
        FleetCarrier underTest = FleetCarrier.builder()
            .build();

        assertThat(underTest.getType()).isEqualTo(StationType.FLEET_CARRIER);
    }

    @Test
    void getName() {
        FleetCarrier underTest = FleetCarrier.builder()
            .carrierId(CARRIER_ID)
            .carrierName(CARRIER_NAME)
            .build();

        assertThat(underTest.getName()).isEqualTo(String.format("%s - %s", CARRIER_ID, CARRIER_NAME));
    }

    @Test
    void getName_nullName() {
        FleetCarrier underTest = FleetCarrier.builder()
            .carrierId(CARRIER_ID)
            .build();

        assertThat(underTest.getName()).isEqualTo(CARRIER_ID);
    }

    @Test
    void getBodyId() {
        FleetCarrier underTest = FleetCarrier.builder()
            .build();

        assertThat(underTest.getBodyId()).isNull();
    }
}
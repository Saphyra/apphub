package com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class StarSystemPositionTest {
    private static final Double X_POS = 232.2432;
    private static final Double Y_POS = 2343.34;
    private static final Double Z_POS = 234.345;

    @Test
    void parse_null() {
        assertThat(StarSystemPosition.parse(null)).isNull();
    }

    @Test
    void parse_wrongArraySize() {
        assertThat(StarSystemPosition.parse(new Double[]{2.3, 53.23})).isNull();
    }

    @Test
    void parse() {
        assertThat(StarSystemPosition.parse(new Double[]{X_POS, Y_POS, Z_POS}))
            .returns(X_POS, StarSystemPosition::getX)
            .returns(Y_POS, StarSystemPosition::getY)
            .returns(Z_POS, StarSystemPosition::getZ);
    }
}
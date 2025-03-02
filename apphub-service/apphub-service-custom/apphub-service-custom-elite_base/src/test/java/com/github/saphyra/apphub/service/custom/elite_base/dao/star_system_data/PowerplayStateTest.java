package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data;

import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.PowerplayState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class PowerplayStateTest {
    @Test
    void parse_null() {
        assertThat(PowerplayState.parse(null)).isNull();
    }

    @Test
    void parse_error() {
        assertThat(catchThrowable(() -> PowerplayState.parse("asd"))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void parse() {
        assertThat(PowerplayState.parse("unoccupied")).isEqualTo(PowerplayState.UNOCCUPIED);
    }
}
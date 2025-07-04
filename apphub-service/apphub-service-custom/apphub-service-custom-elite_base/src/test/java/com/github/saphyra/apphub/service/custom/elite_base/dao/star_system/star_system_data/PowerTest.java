package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data;

import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.Power;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class PowerTest {
    @Test
    void parse_null() {
        assertThat(Power.parse(null)).isNull();
    }

    @Test
    void parse_error() {
        assertThat(catchThrowable(() -> Power.parse("asd"))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void parse() {
        assertThat(Power.parse("Aisling Duval")).isEqualTo(Power.AISLING_DUVAL);
    }
}
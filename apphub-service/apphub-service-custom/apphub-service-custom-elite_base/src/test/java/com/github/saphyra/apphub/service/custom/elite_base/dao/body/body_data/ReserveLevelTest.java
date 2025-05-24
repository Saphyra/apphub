package com.github.saphyra.apphub.service.custom.elite_base.dao.body.body_data;

import com.github.saphyra.apphub.service.custom.elite_base.dao.body.body_data.ReserveLevel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

@ExtendWith(MockitoExtension.class)
class ReserveLevelTest {
    @Test
    void parse_null() {
        assertThat(ReserveLevel.parse(null)).isNull();
    }

    @Test
    void parse_error() {
        assertThat(catchThrowable(() -> ReserveLevel.parse("asd"))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void parse() {
        assertThat(ReserveLevel.parse("DepletedResources")).isEqualTo(ReserveLevel.DEPLETED);
    }
}
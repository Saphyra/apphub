package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system_data.conflict;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class WarStatusTest {
    @Test
    void parse_null() {
        assertThat(WarStatus.parse(null)).isNull();
    }

    @Test
    void parse_error() {
        assertThat(catchThrowable(() -> WarStatus.parse("asd"))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void parse() {
        assertThat(WarStatus.parse("active")).isEqualTo(WarStatus.ACTIVE);
    }
}
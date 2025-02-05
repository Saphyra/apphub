package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class AllegianceTest {
    @Test
    void parse_null() {
        assertThat(Allegiance.parse(null)).isEqualTo(Allegiance.NONE);
    }

    @Test
    void parse_error() {
        assertThat(catchThrowable(() -> Allegiance.parse("asd"))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void parse() {
        assertThat(Allegiance.parse("Alliance")).isEqualTo(Allegiance.ALLIANCE);
    }
}
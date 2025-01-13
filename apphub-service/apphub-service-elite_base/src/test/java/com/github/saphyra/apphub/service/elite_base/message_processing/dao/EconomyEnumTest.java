package com.github.saphyra.apphub.service.elite_base.message_processing.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class EconomyEnumTest {
    @Test
    void parse_null() {
        assertThat(EconomyEnum.parse(null)).isNull();
    }

    @Test
    void parse_error() {
        assertThat(catchThrowable(() -> EconomyEnum.parse("asd"))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void parse() {
        assertThat(EconomyEnum.parse("Refinery")).isEqualTo(EconomyEnum.REFINERY);
    }
}
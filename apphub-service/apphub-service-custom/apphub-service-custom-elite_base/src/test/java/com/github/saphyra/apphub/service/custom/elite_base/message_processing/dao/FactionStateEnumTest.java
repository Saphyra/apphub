package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class FactionStateEnumTest {
    @Test
    void parse_null() {
        assertThat(FactionStateEnum.parse(null)).isEqualTo(FactionStateEnum.NONE);
    }

    @Test
    void parse_error() {
        assertThat(catchThrowable(() -> FactionStateEnum.parse("asd"))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void parse() {
        assertThat(FactionStateEnum.parse("PirateAttack")).isEqualTo(FactionStateEnum.PIRATE_ATTACK);
    }
}
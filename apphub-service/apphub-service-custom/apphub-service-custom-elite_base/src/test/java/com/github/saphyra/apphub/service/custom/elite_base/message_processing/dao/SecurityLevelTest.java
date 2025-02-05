package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class SecurityLevelTest {
    @Test
    void parse_null() {
        assertThat(SecurityLevel.parse(null)).isNull();
    }

    @Test
    void parse_error() {
        assertThat(catchThrowable(() -> SecurityLevel.parse("asd"))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void parse() {
        assertThat(SecurityLevel.parse("$SYSTEM_SECURITY_low;")).isEqualTo(SecurityLevel.LOW);
    }
}
package com.github.saphyra.apphub.service.elite_base.message_processing.dao.body;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

@ExtendWith(MockitoExtension.class)
class BodyTypeTest {
    @Test
    void parse_null() {
        assertThat(BodyType.parse(null)).isNull();
    }

    @Test
    void parse_nullText() {
        assertThat(BodyType.parse("nUlL")).isNull();
    }

    @Test
    void parse_error() {
        assertThat(catchThrowable(() -> BodyType.parse("asd"))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void parse() {
        assertThat(BodyType.parse("pLaNeT")).isEqualTo(BodyType.PLANET);
    }
}
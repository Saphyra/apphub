package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system;

import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

@ExtendWith(MockitoExtension.class)
class StarTypeTest {
    @Test
    void parse_null() {
        assertThat(StarType.parse(null)).isNull();
    }

    @Test
    void parse_wrongInput() {
        assertThat(catchThrowable(() -> StarType.parse("asd"))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void parse() {
        assertThat(StarType.parse("m")).isEqualTo(StarType.M);
    }
}
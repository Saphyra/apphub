package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.body_data.body_ring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

@ExtendWith(MockitoExtension.class)
class RingTypeTest {
    @Test
    void parse_null() {
        assertThat(RingType.parse(null)).isNull();
    }

    @Test
    void parse_error() {
        assertThat(catchThrowable(() -> RingType.parse("asd"))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void parse() {
        assertThat(RingType.parse("eRingClass_MetalRich")).isEqualTo(RingType.METAL_RICH);
    }
}
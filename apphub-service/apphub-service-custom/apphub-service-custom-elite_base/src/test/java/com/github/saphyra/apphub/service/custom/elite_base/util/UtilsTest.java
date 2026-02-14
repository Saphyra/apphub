package com.github.saphyra.apphub.service.custom.elite_base.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UtilsTest {
    @Test
    void nullIfZero() {
        assertThat(Utils.nullIfZero(0)).isNull();

        assertThat(Utils.nullIfZero(3.14)).isEqualTo(3.14);
    }
}
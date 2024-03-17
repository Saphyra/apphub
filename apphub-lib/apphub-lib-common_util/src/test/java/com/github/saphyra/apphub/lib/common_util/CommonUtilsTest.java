package com.github.saphyra.apphub.lib.common_util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommonUtilsTest {
    @Test
    void withLeadingZeros_lessDigits() {
        assertThat(CommonUtils.withLeadingZeros(2, 2)).isEqualTo("02");
    }

    @Test
    void withLeadingZeros_equalDigits() {
        assertThat(CommonUtils.withLeadingZeros(20, 2)).isEqualTo("20");
    }

    @Test
    void withLeadingZeros_moreDigits() {
        assertThat(CommonUtils.withLeadingZeros(2025, 2)).isEqualTo("2025");
    }
}
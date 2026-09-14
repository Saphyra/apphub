package com.github.saphyra.apphub.service.feature.calendar.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CalendarUtilsTest {
    private static final String VALUE = "value";
    private static final String MASKED_VALUE = "*****";

    @Test
    void mask() {
        assertThat(CalendarUtils.mask(true, VALUE, MASKED_VALUE)).isEqualTo(MASKED_VALUE);
        assertThat(CalendarUtils.mask(false, VALUE, MASKED_VALUE)).isEqualTo(VALUE);
    }
}
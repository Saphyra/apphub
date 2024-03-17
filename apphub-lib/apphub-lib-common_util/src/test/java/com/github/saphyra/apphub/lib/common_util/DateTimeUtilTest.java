package com.github.saphyra.apphub.lib.common_util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DateTimeUtilTest {
    private static final int YEAR = 2024;
    private static final int MONTH = 2;
    private static final int DAY = 5;
    private static final LocalDate DATE = LocalDate.of(YEAR, MONTH, DAY);

    @InjectMocks
    private DateTimeUtil underTest;

    @Test
    void formatLocalDate() {
        assertThat(underTest.format(DATE)).isEqualTo("2024-02-05");
    }
}
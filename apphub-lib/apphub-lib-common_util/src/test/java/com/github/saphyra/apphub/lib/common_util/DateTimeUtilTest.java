package com.github.saphyra.apphub.lib.common_util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.stream.Stream;

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

    @ParameterizedTest
    @MethodSource("isBetweenParams")
    void isBetween(LocalDate date, LocalDate start, LocalDate end, boolean expected) {
        assertThat(underTest.isBetween(date, start, end)).isEqualTo(expected);
    }

    static Stream<Arguments> isBetweenParams() {
        return Stream.of(
            Arguments.of(LocalDate.of(2025, 2, 15), LocalDate.of(2025, 2, 15), LocalDate.of(2025, 2, 15), true),
            Arguments.of(LocalDate.of(2025, 2, 15), LocalDate.of(2025, 2, 14), LocalDate.of(2025, 2, 16), true),
            Arguments.of(LocalDate.of(2025, 2, 13), LocalDate.of(2025, 2, 14), LocalDate.of(2025, 2, 16), false),
            Arguments.of(LocalDate.of(2025, 2, 17), LocalDate.of(2025, 2, 14), LocalDate.of(2025, 2, 16), false)
        );
    }
}
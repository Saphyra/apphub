package com.github.saphyra.apphub.lib.common_util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DateTimeConverterTest {
    private static final String VALID_TIME_STRING = "14:30:45";
    private static final LocalTime VALID_TIME = LocalTime.of(14, 30, 45);

    private static final String VALID_DATE_STRING = "2023-12-25";
    private static final LocalDate VALID_DATE = LocalDate.of(2023, 12, 25);

    private static final String VALID_DATE_TIME_STRING = "2023-12-25T14:30:45";
    private static final LocalDateTime VALID_DATE_TIME = LocalDateTime.of(2023, 12, 25, 14, 30, 45);

    @InjectMocks
    private DateTimeConverter dateTimeConverter;

    @Test
    void convertDomain_localTime_null() {
        assertThat(dateTimeConverter.convertDomain((LocalTime) null)).isNull();
    }

    @Test
    void convertDomain_localTime_valid() {
        assertThat(dateTimeConverter.convertDomain(VALID_TIME))
            .isEqualTo(VALID_TIME_STRING);
    }

    @Test
    void convertDomain_localDate_null() {
        assertThat(dateTimeConverter.convertDomain((LocalDate) null)).isNull();
    }

    @Test
    void convertDomain_localDate_valid() {
        assertThat(dateTimeConverter.convertDomain(VALID_DATE))
            .isEqualTo(VALID_DATE_STRING);
    }

    @Test
    void convertDomain_localDateTime_null() {
        assertThat(dateTimeConverter.convertDomain((LocalDateTime) null)).isNull();
    }

    @Test
    void convertDomain_localDateTime_valid() {
        assertThat(dateTimeConverter.convertDomain(VALID_DATE_TIME))
            .isEqualTo(VALID_DATE_TIME_STRING);
    }

    @Test
    void convertToLocalDateTime_null() {
        assertThat(dateTimeConverter.convertToLocalDateTime(null)).isNull();
    }

    @Test
    void convertToLocalDateTime_valid() {
        assertThat(dateTimeConverter.convertToLocalDateTime(VALID_DATE_TIME_STRING))
            .isEqualTo(VALID_DATE_TIME);
    }

    @Test
    void convertToLocalDate_null() {
        assertThat(dateTimeConverter.convertToLocalDate(null)).isNull();
    }

    @Test
    void convertToLocalDate_valid() {
        assertThat(dateTimeConverter.convertToLocalDate(VALID_DATE_STRING))
            .isEqualTo(VALID_DATE);
    }

    @Test
    void convertToLocalTime_null() {
        assertThat(dateTimeConverter.convertToLocalTime(null)).isNull();
    }

    @Test
    void convertToLocalTime_valid() {
        assertThat(dateTimeConverter.convertToLocalTime(VALID_TIME_STRING))
            .isEqualTo(VALID_TIME);
    }
}
package com.github.saphyra.apphub.lib.common_util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DateTimeConverterTest {
    private static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.now();

    @InjectMocks
    private DateTimeConverter underTest;

    @Test
    void convertDomain_localDateTime_null() {
        assertThat(underTest.convertDomain(null)).isNull();
    }

    @Test
    void convertDomain_localDateTime() {
        assertThat(underTest.convertDomain(LOCAL_DATE_TIME)).isEqualTo(LOCAL_DATE_TIME.toString());
    }

    @Test
    void convertToLocalDateTime_null() {
        assertThat(underTest.convertToLocalDateTime(null)).isNull();
    }

    @Test
    void convertToLocalDateTime() {
        assertThat(underTest.convertToLocalDateTime(LOCAL_DATE_TIME.toString())).isEqualTo(LOCAL_DATE_TIME);
    }
}
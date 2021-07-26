package com.github.saphyra.apphub.service.admin_panel.error_report.service.overview;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class CustomLocalDateTimeParserTest {
    @InjectMocks
    private CustomLocalDateTimeParser underTest;

    @Test
    public void convertFull() {
        String in = "1996-02-15 16:34:35.324";

        LocalDateTime result = underTest.parse(in);

        assertThat(result.getYear()).isEqualTo(1996);
        assertThat(result.getMonthValue()).isEqualTo(2);
        assertThat(result.getDayOfMonth()).isEqualTo(15);
        assertThat(result.getHour()).isEqualTo(16);
        assertThat(result.getMinute()).isEqualTo(34);
        assertThat(result.getSecond()).isEqualTo(35);
        assertThat(result.getNano()).isEqualTo(324);
    }

    @Test
    public void convertWithoutNano() {
        String in = "1996-02-15 16:34:35";

        LocalDateTime result = underTest.parse(in);

        assertThat(result.getYear()).isEqualTo(1996);
        assertThat(result.getMonthValue()).isEqualTo(2);
        assertThat(result.getDayOfMonth()).isEqualTo(15);
        assertThat(result.getHour()).isEqualTo(16);
        assertThat(result.getMinute()).isEqualTo(34);
        assertThat(result.getSecond()).isEqualTo(35);
        assertThat(result.getNano()).isEqualTo(0);
    }

    @Test
    public void convertWithoutSecond() {
        String in = "1996-02-15 16:34";

        LocalDateTime result = underTest.parse(in);

        assertThat(result.getYear()).isEqualTo(1996);
        assertThat(result.getMonthValue()).isEqualTo(2);
        assertThat(result.getDayOfMonth()).isEqualTo(15);
        assertThat(result.getHour()).isEqualTo(16);
        assertThat(result.getMinute()).isEqualTo(34);
        assertThat(result.getSecond()).isEqualTo(0);
        assertThat(result.getNano()).isEqualTo(0);
    }

    @Test
    public void convertWithoutMinute() {
        String in = "1996-02-15 16";

        LocalDateTime result = underTest.parse(in);

        assertThat(result.getYear()).isEqualTo(1996);
        assertThat(result.getMonthValue()).isEqualTo(2);
        assertThat(result.getDayOfMonth()).isEqualTo(15);
        assertThat(result.getHour()).isEqualTo(16);
        assertThat(result.getMinute()).isEqualTo(0);
        assertThat(result.getSecond()).isEqualTo(0);
        assertThat(result.getNano()).isEqualTo(0);
    }

    @Test
    public void convertWithoutHour() {
        String in = "1996-02-15";

        LocalDateTime result = underTest.parse(in);

        assertThat(result.getYear()).isEqualTo(1996);
        assertThat(result.getMonthValue()).isEqualTo(2);
        assertThat(result.getDayOfMonth()).isEqualTo(15);
        assertThat(result.getHour()).isEqualTo(0);
        assertThat(result.getMinute()).isEqualTo(0);
        assertThat(result.getSecond()).isEqualTo(0);
        assertThat(result.getNano()).isEqualTo(0);
    }

    @Test
    public void convertWithoutDay() {
        String in = "1996-02";

        LocalDateTime result = underTest.parse(in);

        assertThat(result.getYear()).isEqualTo(1996);
        assertThat(result.getMonthValue()).isEqualTo(2);
        assertThat(result.getDayOfMonth()).isEqualTo(1);
        assertThat(result.getHour()).isEqualTo(0);
        assertThat(result.getMinute()).isEqualTo(0);
        assertThat(result.getSecond()).isEqualTo(0);
        assertThat(result.getNano()).isEqualTo(0);
    }

    @Test
    public void convertWithoutMonth() {
        String in = "1996";

        LocalDateTime result = underTest.parse(in);

        assertThat(result.getYear()).isEqualTo(1996);
        assertThat(result.getMonthValue()).isEqualTo(1);
        assertThat(result.getDayOfMonth()).isEqualTo(1);
        assertThat(result.getHour()).isEqualTo(0);
        assertThat(result.getMinute()).isEqualTo(0);
        assertThat(result.getSecond()).isEqualTo(0);
        assertThat(result.getNano()).isEqualTo(0);
    }

    @Test
    public void convertEmpty() {
        String in = " ";

        LocalDateTime result = underTest.parse(in);

        assertThat(result.getYear()).isEqualTo(1970);
        assertThat(result.getMonthValue()).isEqualTo(1);
        assertThat(result.getDayOfMonth()).isEqualTo(1);
        assertThat(result.getHour()).isEqualTo(0);
        assertThat(result.getMinute()).isEqualTo(0);
        assertThat(result.getSecond()).isEqualTo(0);
        assertThat(result.getNano()).isEqualTo(0);
    }

    @Test
    public void convertNull() {
        String in = null;

        LocalDateTime result = underTest.parse(in);

        assertThat(result.getYear()).isEqualTo(1970);
        assertThat(result.getMonthValue()).isEqualTo(1);
        assertThat(result.getDayOfMonth()).isEqualTo(1);
        assertThat(result.getHour()).isEqualTo(0);
        assertThat(result.getMinute()).isEqualTo(0);
        assertThat(result.getSecond()).isEqualTo(0);
        assertThat(result.getNano()).isEqualTo(0);
    }
}
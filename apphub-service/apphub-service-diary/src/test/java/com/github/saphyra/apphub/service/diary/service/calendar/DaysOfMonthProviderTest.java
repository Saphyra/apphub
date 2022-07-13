package com.github.saphyra.apphub.service.diary.service.calendar;

import org.junit.Test;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DaysOfMonthProviderTest {
    private static final LocalDate DATE = LocalDate.now();

    private final DaysOfMonthProvider underTest = new DaysOfMonthProvider();

    @Test
    public void getDaysOfMonth() {
        List<LocalDate> result = underTest.getDaysOfMonth(DATE);

        YearMonth yearMonthObject = YearMonth.of(DATE.getYear(), DATE.getMonth());
        int daysInMonth = yearMonthObject.lengthOfMonth();

        assertThat(result.size() % 7).isZero();
        assertThat(result.contains(LocalDate.of(DATE.getYear(), DATE.getMonth(), 1)));
        assertThat(result.contains(LocalDate.of(DATE.getYear(), DATE.getMonth(), daysInMonth)));
    }
}
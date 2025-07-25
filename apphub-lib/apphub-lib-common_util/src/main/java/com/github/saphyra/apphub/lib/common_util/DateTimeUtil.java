package com.github.saphyra.apphub.lib.common_util;

import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
public class DateTimeUtil {
    private static final String DATE_FORMAT = "%s-%s-%s";
    private static final String TIME_FORMAT = "%s:%s:%s";
    private static final String SHORT_TIME_FORMAT = "%s:%s";

    public LocalTime getCurrentTime() {
        return LocalTime.now(ZoneOffset.UTC);
    }

    public LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

    public long toEpochSecond(LocalDateTime localDateTime) {
        return Optional.ofNullable(localDateTime)
            .map(ld -> ld.toEpochSecond(ZoneOffset.UTC))
            .orElse(0L);
    }

    public LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    public Long getCurrentTimeEpochMillis() {
        return getCurrentDateTime()
            .toInstant(ZoneOffset.UTC)
            .toEpochMilli();
    }

    public String format(LocalDateTime dateTime) {
        return format(dateTime, true);
    }

    public String format(LocalDateTime dateTime, boolean withSeconds) {
        return format(dateTime.toLocalDate()) + " " + (withSeconds ? format(dateTime.toLocalTime()) : formatWithoutSeconds(dateTime.toLocalTime()));
    }

    public String format(LocalDate date) {
        return DATE_FORMAT.formatted(
            date.getYear(),
            CommonUtils.withLeadingZeros(date.getMonthValue(), 2),
            CommonUtils.withLeadingZeros(date.getDayOfMonth(), 2)
        );
    }

    public String format(LocalTime time) {
        return TIME_FORMAT.formatted(
            CommonUtils.withLeadingZeros(time.getHour(), 2),
            CommonUtils.withLeadingZeros(time.getMinute(), 2),
            CommonUtils.withLeadingZeros(time.getSecond(), 2)
        );
    }

    public String formatWithoutSeconds(LocalTime time) {
        return SHORT_TIME_FORMAT.formatted(
            CommonUtils.withLeadingZeros(time.getHour(), 2),
            CommonUtils.withLeadingZeros(time.getMinute(), 2)
        );
    }

    public OffsetDateTime getCurrentOffsetDateTime() {
        return OffsetDateTime.now(ZoneOffset.UTC);
    }

    //TODO unit test
    public LocalDate earliest(Collection<LocalDate> keySet) {
        return keySet.stream()
            .min(LocalDate::compareTo)
            .orElseThrow(() -> new IllegalArgumentException("The collection of LocalDate is empty"));
    }

    //TODO unit test
    public LocalDate earlier(@NonNull LocalDate date1, @NonNull LocalDate date2) {
        return earliest(List.of(date1, date2));
    }

    //TODO unit test
    public LocalDate latest(Collection<LocalDate> keySet) {
        return keySet.stream()
            .max(LocalDate::compareTo)
            .orElseThrow(() -> new IllegalArgumentException("The collection of LocalDate is empty"));
    }

    //TODO unit test
    public LocalDate later(@NonNull LocalDate date1, @NonNull LocalDate date2) {
        return latest(List.of(date1, date2));
    }

    public boolean isBetween(LocalDate date, LocalDate startDate, LocalDate endDate) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
}

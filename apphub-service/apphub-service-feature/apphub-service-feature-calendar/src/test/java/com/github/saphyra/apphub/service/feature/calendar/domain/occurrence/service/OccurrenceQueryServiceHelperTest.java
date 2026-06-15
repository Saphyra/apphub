package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.feature.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import lombok.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OccurrenceQueryServiceHelperTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final LocalDate START_DATE = LocalDate.of(2026, 6, 1);
    private static final LocalDate END_DATE = LocalDate.of(2026, 7, 1);
    private static final LocalDate CURRENT_DATE = LocalDate.of(2026, 6, 15);
    private static final @NonNull UUID EVENT_ID = UUID.randomUUID();
    private static final @NonNull UUID OCCURRENCE_ID = UUID.randomUUID();

    @Mock
    private OccurrenceDao occurrenceDao;

    @InjectMocks
    private OccurrenceQueryServiceHelper underTest;

    @Mock
    private Event event;

    @Mock
    private Occurrence occurrence1;

    @Mock
    private Occurrence occurrence2;

    @Mock
    private Occurrence occurrence3;

    @Test
    void getOccurrencesBetween() {
        given(occurrenceDao.getByBuckets(USER_ID, List.of("2026-6", "2026-7"))).willReturn(List.of(occurrence1, occurrence2, occurrence3));
        given(occurrence1.getDate()).willReturn(CURRENT_DATE);
        given(occurrence2.getDate()).willReturn(END_DATE.plusDays(1));
        given(occurrence3.getDate()).willReturn(START_DATE.minusDays(1));

        assertThat(underTest.getOccurrencesBetween(USER_ID, START_DATE, END_DATE)).containsExactlyInAnyOrder(occurrence1);
    }

    @Test
    void getOccurrencesToAdd() {
        Occurrence occurrence = createOccurrence(CURRENT_DATE, OccurrenceStatus.PENDING, 0, false);

        assertThat(underTest.getOccurrencesToAdd(event, occurrence, CURRENT_DATE, START_DATE, END_DATE)).containsExactly(occurrence);
    }

    @Test
    void getOccurrencesToAdd_addExpiredToCurrentDate() {
        Occurrence occurrence = createOccurrence(CURRENT_DATE.minusDays(1), OccurrenceStatus.EXPIRED, 0, false);

        assertThat(underTest.getOccurrencesToAdd(event, occurrence, CURRENT_DATE, START_DATE, END_DATE)).containsExactlyInAnyOrder(occurrence, occurrence.toBuilder().date(CURRENT_DATE).build());
    }

    @Test
    void getOccurrencesToAdd_dontAddDoneToCurrentDate() {
        Occurrence occurrence = createOccurrence(CURRENT_DATE.minusDays(1), OccurrenceStatus.DONE, 0, false);

        assertThat(underTest.getOccurrencesToAdd(event, occurrence, CURRENT_DATE, START_DATE, END_DATE)).containsExactlyInAnyOrder(occurrence);
    }

    @Test
    void getOccurrencesToAdd_addReminderToDefinedDate() {
        Occurrence occurrence = createOccurrence(CURRENT_DATE.plusDays(4), OccurrenceStatus.PENDING, 3, false);

        assertThat(underTest.getOccurrencesToAdd(event, occurrence, CURRENT_DATE, START_DATE, END_DATE)).containsExactlyInAnyOrder(
            occurrence,
            occurrence.toBuilder()
                .date(CURRENT_DATE.plusDays(1))
                .status(OccurrenceStatus.REMINDER)
                .build()
        );
    }

    @Test
    void getOccurrencesToAdd_addReminderToCurrentDate() {
        Occurrence occurrence = createOccurrence(CURRENT_DATE.plusDays(1), OccurrenceStatus.PENDING, 3, false);

        assertThat(underTest.getOccurrencesToAdd(event, occurrence, CURRENT_DATE, START_DATE, END_DATE)).containsExactlyInAnyOrder(
            occurrence,
            occurrence.toBuilder()
                .date(CURRENT_DATE)
                .status(OccurrenceStatus.REMINDER)
                .build()
        );
    }

    @Test
    void getOccurrencesToAdd_dontAddReminderIfReminded() {
        Occurrence occurrence = createOccurrence(CURRENT_DATE.plusDays(4), OccurrenceStatus.PENDING, 3, true);

        assertThat(underTest.getOccurrencesToAdd(event, occurrence, CURRENT_DATE, START_DATE, END_DATE)).containsExactlyInAnyOrder(occurrence);
    }

    private Occurrence createOccurrence(LocalDate date, OccurrenceStatus status, int remindMeBeforeDays, boolean reminded) {
        return Occurrence.builder()
            .userId(USER_ID)
            .eventId(EVENT_ID)
            .occurrenceId(OCCURRENCE_ID)
            .date(date)
            .status(status)
            .note("")
            .remindMeBeforeDays(remindMeBeforeDays)
            .reminded(reminded)
            .build();
    }
}
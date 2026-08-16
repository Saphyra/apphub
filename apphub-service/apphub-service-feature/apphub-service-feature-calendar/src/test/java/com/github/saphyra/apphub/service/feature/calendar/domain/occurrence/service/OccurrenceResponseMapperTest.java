package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.feature.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.api.feature.calendar.model.RepetitionType;
import com.github.saphyra.apphub.api.feature.calendar.model.response.OccurrenceResponse;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class OccurrenceResponseMapperTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID OTHER_USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();

    private static final LocalDate START_DATE = LocalDate.of(2026, 6, 1);
    private static final LocalDate OCCURRENCE_DATE = LocalDate.of(2026, 6, 3);
    private static final LocalTime EVENT_TIME = LocalTime.of(15, 0);
    private static final LocalTime OCCURRENCE_TIME = LocalTime.of(14, 30);

    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String NOTE = "note";

    @Mock
    private OccurrenceDao occurrenceDao;

    @InjectMocks
    private OccurrenceResponseMapper underTest;

    @Test
    void autoDoneOccurrence() {
        Occurrence expiredOccurrence = createOccurrence(OTHER_USER_ID, false, OccurrenceStatus.EXPIRED, 2, false, true);
        Event event = createEvent(false, false, true);

        OccurrenceResponse result = underTest.toResponse(USER_ID, event, expiredOccurrence);

        assertThat(result.getStatus()).isEqualTo(OccurrenceStatus.DONE);
        assertThat(expiredOccurrence.getStatus()).isEqualTo(OccurrenceStatus.DONE);
        then(occurrenceDao).should().save(expiredOccurrence);
        assertThat(result.getAutoDone()).isTrue();
    }

    @Test
    void maskedEvent() {
        Event maskedEvent = createEvent(true, true, false);
        Occurrence occurrence = createOccurrence(USER_ID, false, OccurrenceStatus.PENDING, 2, false, null);

        OccurrenceResponse result = underTest.toResponse(USER_ID, maskedEvent, occurrence);

        assertThat(result.getTitle()).isEqualTo("?");
        assertThat(result.getContent()).isEqualTo("");
        assertThat(result.getEventArchived()).isTrue();
        assertThat(result.getTime()).isEqualTo(OCCURRENCE_TIME);
        assertThat(result.getNote()).isEqualTo(NOTE);
        assertThat(result.getRemindMeBeforeDays()).isEqualTo(2);
        assertThat(result.getShared()).isFalse();
    }

    @Test
    void maskedOccurrence() {
        Event event = createEvent(false, false, false);
        Occurrence maskedOccurrence = createOccurrence(USER_ID, true, OccurrenceStatus.PENDING, 2, true, null);

        OccurrenceResponse result = underTest.toResponse(USER_ID, event, maskedOccurrence);

        assertThat(result.getTime()).isNull();
        assertThat(result.getNote()).isEqualTo("");
        assertThat(result.getTitle()).isEqualTo("title");
        assertThat(result.getContent()).isEqualTo("content");
        assertThat(result.getReminded()).isTrue();
    }

    @Test
    void maskedEventAndOccurrence() {
        Event maskedEvent = createEvent(true, false, false);
        Occurrence maskedOccurrence = createOccurrence(OTHER_USER_ID, true, OccurrenceStatus.PENDING, 2, false, null);

        OccurrenceResponse result = underTest.toResponse(USER_ID, maskedEvent, maskedOccurrence);

        assertThat(result.getTitle()).isEqualTo("?");
        assertThat(result.getContent()).isEqualTo("");
        assertThat(result.getTime()).isNull();
        assertThat(result.getNote()).isEqualTo("");
        assertThat(result.getShared()).isTrue();
    }

    @Test
    void normalMapping() {
        Event event = createEvent(false, false, false);
        Occurrence occurrence = createOccurrence(USER_ID, false, OccurrenceStatus.PENDING, null, false, null);

        OccurrenceResponse result = underTest.toResponse(USER_ID, event, occurrence);

        assertThat(result.getOccurrenceId()).isEqualTo(OCCURRENCE_ID);
        assertThat(result.getEventId()).isEqualTo(EVENT_ID);
        assertThat(result.getDate()).isEqualTo(OCCURRENCE_DATE);
        assertThat(result.getTime()).isEqualTo(OCCURRENCE_TIME);
        assertThat(result.getStatus()).isEqualTo(OccurrenceStatus.PENDING);
        assertThat(result.getTitle()).isEqualTo(TITLE);
        assertThat(result.getContent()).isEqualTo(CONTENT);
        assertThat(result.getNote()).isEqualTo(NOTE);
        assertThat(result.getRemindMeBeforeDays()).isEqualTo(3);
        assertThat(result.getReminded()).isFalse();
        assertThat(result.getEventArchived()).isFalse();
        assertThat(result.getAutoDone()).isFalse();
        assertThat(result.getShared()).isFalse();
        then(occurrenceDao).should(never()).save(any(Occurrence.class));
    }

    private Event createEvent(boolean masked, boolean archived, boolean autoDone) {
        return Event.builder()
            .eventId(EVENT_ID)
            .userId(USER_ID)
            .repetitionType(RepetitionType.ONE_TIME)
            .repetitionData(null)
            .repeatForDays(0)
            .startDate(START_DATE)
            .endDate(null)
            .time(EVENT_TIME)
            .title(TITLE)
            .content(CONTENT)
            .remindMeBeforeDays(3)
            .expirationNotified(false)
            .archived(archived)
            .autoDone(autoDone)
            .build()
            .setMasked(masked);
    }

    private Occurrence createOccurrence(UUID userId, boolean masked, OccurrenceStatus status, Integer remindMeBeforeDays, boolean reminded, Boolean autoDone) {
        return Occurrence.builder()
            .userId(userId)
            .eventId(EVENT_ID)
            .occurrenceId(OCCURRENCE_ID)
            .date(OCCURRENCE_DATE)
            .time(OCCURRENCE_TIME)
            .status(status)
            .note(NOTE)
            .remindMeBeforeDays(remindMeBeforeDays)
            .reminded(reminded)
            .autoDone(autoDone)
            .build()
            .setMasked(masked);
    }
}
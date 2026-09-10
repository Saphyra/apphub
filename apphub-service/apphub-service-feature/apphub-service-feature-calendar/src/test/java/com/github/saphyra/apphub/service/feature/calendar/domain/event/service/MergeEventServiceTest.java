package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.api.feature.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.api.feature.calendar.model.RepetitionType;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class MergeEventServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PARENT_EVENT_ID = UUID.randomUUID();
    private static final UUID MATCHING_EVENT_ID_1 = UUID.randomUUID();
    private static final UUID MATCHING_EVENT_ID_2 = UUID.randomUUID();
    private static final UUID DIFFERENT_TITLE_EVENT_ID = UUID.randomUUID();
    private static final UUID DIFFERENT_TYPE_EVENT_ID = UUID.randomUUID();

    @Mock
    private EventDao eventDao;

    @Mock
    private OccurrenceDao occurrenceDao;

    @Mock
    private DeleteEventService deleteEventService;

    @InjectMocks
    private MergeEventService underTest;

    @Test
    void merge_invalidRepetitionType_throwsException() {
        Event parent = event(PARENT_EVENT_ID, "Parent", RepetitionType.EVERY_X_DAYS, "content", LocalTime.NOON, 2);
        given(eventDao.findByIdValidated(USER_ID, PARENT_EVENT_ID)).willReturn(parent);

        ExceptionValidator.validateInvalidParam(() -> underTest.merge(USER_ID, PARENT_EVENT_ID), "eventId", "invalid type");
    }

    @Test
    void merge() {
        Event parent = event(PARENT_EVENT_ID, " Parent ", RepetitionType.ONE_TIME, "parent-content", LocalTime.of(8, 0), 1);
        Event matching1 = event(MATCHING_EVENT_ID_1, "parent", RepetitionType.ONE_TIME, "content-1", LocalTime.of(12, 30), 5);
        Event matching2 = event(MATCHING_EVENT_ID_2, "PARENT", RepetitionType.ONE_TIME, "   ", null, 0);
        Event differentTitle = event(DIFFERENT_TITLE_EVENT_ID, "Other", RepetitionType.ONE_TIME, "ignored", LocalTime.MIDNIGHT, 9);
        Event differentType = event(DIFFERENT_TYPE_EVENT_ID, "parent", RepetitionType.DAYS_OF_WEEK, "ignored", LocalTime.MIDNIGHT, 9);

        Occurrence occurrence1 = occurrence(MATCHING_EVENT_ID_1, "note-1", null, null);
        Occurrence occurrence2 = occurrence(MATCHING_EVENT_ID_1, "", LocalTime.of(11, 5), 7);
        Occurrence occurrence3 = occurrence(MATCHING_EVENT_ID_2, "note-3", null, null);

        given(eventDao.findByIdValidated(USER_ID, PARENT_EVENT_ID)).willReturn(parent);
        given(eventDao.getByUserId(USER_ID)).willReturn(List.of(parent, matching1, matching2, differentTitle, differentType));
        given(occurrenceDao.getByEventId(MATCHING_EVENT_ID_1)).willReturn(List.of(occurrence1, occurrence2));
        given(occurrenceDao.getByEventId(MATCHING_EVENT_ID_2)).willReturn(List.of(occurrence3));

        underTest.merge(USER_ID, PARENT_EVENT_ID);

        ArgumentCaptor<List<UUID>> deletedEventIdsCaptor = ArgumentCaptor.forClass(List.class);
        then(deleteEventService).should().delete(org.mockito.ArgumentMatchers.eq(USER_ID), deletedEventIdsCaptor.capture());
        assertThat(deletedEventIdsCaptor.getValue()).containsExactly(MATCHING_EVENT_ID_1, MATCHING_EVENT_ID_2);

        ArgumentCaptor<List<Occurrence>> deletedOccurrencesCaptor = ArgumentCaptor.forClass(List.class);
        then(occurrenceDao).should().delete(deletedOccurrencesCaptor.capture());
        assertThat(deletedOccurrencesCaptor.getValue()).containsExactly(occurrence1, occurrence2, occurrence3);

        ArgumentCaptor<List<Occurrence>> savedOccurrencesCaptor = ArgumentCaptor.forClass(List.class);
        then(occurrenceDao).should().save(savedOccurrencesCaptor.capture());
        List<Occurrence> savedOccurrences = savedOccurrencesCaptor.getValue();

        assertThat(savedOccurrences).hasSize(3);

        assertThat(savedOccurrences.getFirst().getEventId()).isEqualTo(PARENT_EVENT_ID);
        assertThat(savedOccurrences.getFirst().getNote()).isEqualTo("content-1\n\nnote-1");
        assertThat(savedOccurrences.getFirst().getTime()).isEqualTo(LocalTime.of(12, 30));
        assertThat(savedOccurrences.getFirst().getRemindMeBeforeDays()).isEqualTo(5);
        assertThat(savedOccurrences.getFirst().getAutoDone()).isTrue();

        assertThat(savedOccurrences.get(1).getEventId()).isEqualTo(PARENT_EVENT_ID);
        assertThat(savedOccurrences.get(1).getNote()).isEqualTo("content-1");
        assertThat(savedOccurrences.get(1).getTime()).isEqualTo(LocalTime.of(11, 5));
        assertThat(savedOccurrences.get(1).getRemindMeBeforeDays()).isEqualTo(7);
        assertThat(savedOccurrences.get(1).getAutoDone()).isTrue();

        assertThat(savedOccurrences.get(2).getEventId()).isEqualTo(PARENT_EVENT_ID);
        assertThat(savedOccurrences.get(2).getNote()).isEqualTo("note-3");
        assertThat(savedOccurrences.get(2).getTime()).isNull();
        assertThat(savedOccurrences.get(2).getRemindMeBeforeDays()).isZero();
        assertThat(savedOccurrences.get(2).getAutoDone()).isTrue();

        then(occurrenceDao).should().getByEventId(MATCHING_EVENT_ID_1);
        then(occurrenceDao).should().getByEventId(MATCHING_EVENT_ID_2);
        then(occurrenceDao).should(never()).getByEventId(DIFFERENT_TITLE_EVENT_ID);
        then(occurrenceDao).should(never()).getByEventId(DIFFERENT_TYPE_EVENT_ID);
    }

    private Event event(UUID eventId, String title, RepetitionType repetitionType, String content, LocalTime time, Integer remindMeBeforeDays) {
        return Event.builder()
            .eventId(eventId)
            .userId(USER_ID)
            .repetitionType(repetitionType)
            .repeatForDays(1)
            .startDate(LocalDate.now())
            .title(title)
            .content(content)
            .time(time)
            .remindMeBeforeDays(remindMeBeforeDays)
            .autoDone(true)
            .build();
    }

    private Occurrence occurrence(UUID eventId, String note, LocalTime time, Integer remindMeBeforeDays) {
        return Occurrence.builder()
            .userId(USER_ID)
            .eventId(eventId)
            .occurrenceId(UUID.randomUUID())
            .date(LocalDate.now())
            .status(OccurrenceStatus.PENDING)
            .note(note)
            .time(time)
            .remindMeBeforeDays(remindMeBeforeDays)
            .autoDone(null)
            .build();
    }
}
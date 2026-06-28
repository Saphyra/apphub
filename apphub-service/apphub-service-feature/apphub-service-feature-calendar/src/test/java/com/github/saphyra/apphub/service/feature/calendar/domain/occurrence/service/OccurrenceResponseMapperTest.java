package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.feature.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.api.feature.calendar.model.RepetitionType;
import com.github.saphyra.apphub.api.feature.calendar.model.response.OccurrenceResponse;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class OccurrenceResponseMapperTest {
	private static final UUID USER_ID = UUID.randomUUID();
	private static final UUID EVENT_ID_1 = UUID.randomUUID();
	private static final UUID EVENT_ID_2 = UUID.randomUUID();
	private static final UUID OCCURRENCE_ID_1 = UUID.randomUUID();
	private static final UUID OCCURRENCE_ID_2 = UUID.randomUUID();
	private static final LocalDate DATE_1 = LocalDate.of(2030, 1, 2);
	private static final LocalDate DATE_2 = LocalDate.of(2030, 1, 3);
	private static final LocalTime EVENT_TIME_1 = LocalTime.of(8, 15);
	private static final LocalTime EVENT_TIME_2 = LocalTime.of(9, 30);
	private static final LocalTime OCCURRENCE_TIME_2 = LocalTime.of(10, 45);
	private static final OccurrenceStatus STATUS_1 = OccurrenceStatus.PENDING;
	private static final OccurrenceStatus STATUS_2 = OccurrenceStatus.DONE;
	private static final String NOTE_1 = "note-1";
	private static final String NOTE_2 = "note-2";
	private static final Integer EVENT_REMIND_ME_BEFORE_DAYS_1 = 2;
	private static final Integer EVENT_REMIND_ME_BEFORE_DAYS_2 = 4;
	private static final Integer OCCURRENCE_REMIND_ME_BEFORE_DAYS_2 = 7;

	@Mock
	private EventDao eventDao;

	@Mock
	private OccurrenceDao occurrenceDao;

	@InjectMocks
	private OccurrenceResponseMapper underTest;

	@Test
	void toResponse_userId_occurrence() {
		Event event = createEvent(EVENT_ID_1, EVENT_TIME_1, EVENT_REMIND_ME_BEFORE_DAYS_1, true, "title-1", "content-1", true);
		Occurrence occurrence = createOccurrence(EVENT_ID_1, OCCURRENCE_ID_1, DATE_1, null, OccurrenceStatus.EXPIRED, NOTE_1, null, false, null);
		given(eventDao.findByIdValidated(USER_ID, EVENT_ID_1)).willReturn(event);

		OccurrenceResponse result = underTest.toResponse(USER_ID, occurrence);

		assertThat(result)
			.returns(OCCURRENCE_ID_1, OccurrenceResponse::getOccurrenceId)
			.returns(EVENT_ID_1, OccurrenceResponse::getEventId)
			.returns(DATE_1, OccurrenceResponse::getDate)
			.returns(EVENT_TIME_1, OccurrenceResponse::getTime)
			.returns(OccurrenceStatus.DONE, OccurrenceResponse::getStatus)
			.returns("title-1", OccurrenceResponse::getTitle)
			.returns("content-1", OccurrenceResponse::getContent)
			.returns(NOTE_1, OccurrenceResponse::getNote)
			.returns(EVENT_REMIND_ME_BEFORE_DAYS_1, OccurrenceResponse::getRemindMeBeforeDays)
			.returns(false, OccurrenceResponse::getReminded)
			.returns(true, OccurrenceResponse::getEventArchived)
			.returns(true, OccurrenceResponse::getAutoDone);

		then(occurrenceDao).should().save(occurrence);
	}

	@Test
	void toResponse_userId_occurrences() {
		Event event1 = createEvent(EVENT_ID_1, EVENT_TIME_1, EVENT_REMIND_ME_BEFORE_DAYS_1, false, "title-1", "content-1", false);
		Event event2 = createEvent(EVENT_ID_2, EVENT_TIME_2, EVENT_REMIND_ME_BEFORE_DAYS_2, true, "title-2", "content-2", false);
		Occurrence occurrence1 = createOccurrence(EVENT_ID_1, OCCURRENCE_ID_1, DATE_1, null, STATUS_1, NOTE_1, null, false, true);
		Occurrence occurrence2 = createOccurrence(EVENT_ID_2, OCCURRENCE_ID_2, DATE_2, OCCURRENCE_TIME_2, STATUS_2, NOTE_2, OCCURRENCE_REMIND_ME_BEFORE_DAYS_2, true, true);
		given(eventDao.getByIds(USER_ID, List.of(EVENT_ID_1, EVENT_ID_2))).willReturn(List.of(event1, event2));

		List<OccurrenceResponse> result = underTest.toResponse(USER_ID, List.of(occurrence1, occurrence2));

		assertThat(result).hasSize(2);
		assertThat(result.get(0))
			.returns(OCCURRENCE_ID_1, OccurrenceResponse::getOccurrenceId)
			.returns(EVENT_ID_1, OccurrenceResponse::getEventId)
			.returns(DATE_1, OccurrenceResponse::getDate)
			.returns(EVENT_TIME_1, OccurrenceResponse::getTime)
			.returns(STATUS_1, OccurrenceResponse::getStatus)
			.returns("title-1", OccurrenceResponse::getTitle)
			.returns("content-1", OccurrenceResponse::getContent)
			.returns(NOTE_1, OccurrenceResponse::getNote)
			.returns(EVENT_REMIND_ME_BEFORE_DAYS_1, OccurrenceResponse::getRemindMeBeforeDays)
			.returns(false, OccurrenceResponse::getReminded)
			.returns(false, OccurrenceResponse::getEventArchived)
			.returns(true, OccurrenceResponse::getAutoDone);
		assertThat(result.get(1))
			.returns(OCCURRENCE_ID_2, OccurrenceResponse::getOccurrenceId)
			.returns(EVENT_ID_2, OccurrenceResponse::getEventId)
			.returns(DATE_2, OccurrenceResponse::getDate)
			.returns(OCCURRENCE_TIME_2, OccurrenceResponse::getTime)
			.returns(STATUS_2, OccurrenceResponse::getStatus)
			.returns("title-2", OccurrenceResponse::getTitle)
			.returns("content-2", OccurrenceResponse::getContent)
			.returns(NOTE_2, OccurrenceResponse::getNote)
			.returns(OCCURRENCE_REMIND_ME_BEFORE_DAYS_2, OccurrenceResponse::getRemindMeBeforeDays)
			.returns(true, OccurrenceResponse::getReminded)
			.returns(true, OccurrenceResponse::getEventArchived)
			.returns(true, OccurrenceResponse::getAutoDone);

		then(eventDao).should().getByIds(USER_ID, List.of(EVENT_ID_1, EVENT_ID_2));
	}

	@Test
	void toResponse_events() {
		Event event1 = createEvent(EVENT_ID_1, EVENT_TIME_1, EVENT_REMIND_ME_BEFORE_DAYS_1, false, "title-1", "content-1", true);
		Event event2 = createEvent(EVENT_ID_2, EVENT_TIME_2, EVENT_REMIND_ME_BEFORE_DAYS_2, true, "title-2", "content-2", true);
		Occurrence occurrence1 = createOccurrence(EVENT_ID_1, OCCURRENCE_ID_1, DATE_1, null, STATUS_1, NOTE_1, null, false, null);
		Occurrence occurrence2 = createOccurrence(EVENT_ID_2, OCCURRENCE_ID_2, DATE_2, OCCURRENCE_TIME_2, STATUS_2, NOTE_2, OCCURRENCE_REMIND_ME_BEFORE_DAYS_2, true, null);

		List<OccurrenceResponse> result = underTest.toResponse(Map.of(EVENT_ID_1, event1, EVENT_ID_2, event2), List.of(occurrence1, occurrence2));

		assertThat(result).hasSize(2);
		assertThat(result.get(0).getTime()).isEqualTo(EVENT_TIME_1);
		assertThat(result.get(0).getRemindMeBeforeDays()).isEqualTo(EVENT_REMIND_ME_BEFORE_DAYS_1);
		assertThat(result.get(1).getTime()).isEqualTo(OCCURRENCE_TIME_2);
		assertThat(result.get(1).getRemindMeBeforeDays()).isEqualTo(OCCURRENCE_REMIND_ME_BEFORE_DAYS_2);
	}

	private Event createEvent(UUID eventId, LocalTime time, Integer remindMeBeforeDays, boolean archived, String title, String content, boolean autoDone) {
		return Event.builder()
			.eventId(eventId)
			.userId(USER_ID)
			.time(time)
			.remindMeBeforeDays(remindMeBeforeDays)
			.title(title)
			.content(content)
			.archived(archived)
			.repetitionType(RepetitionType.ONE_TIME)
			.repetitionData(null)
			.repeatForDays(0)
			.startDate(LocalDate.of(2030, 1, 1))
			.endDate(null)
			.expirationNotified(false)
			.autoDone(autoDone)
			.build();
	}

	private Occurrence createOccurrence(UUID eventId, UUID occurrenceId, LocalDate date, LocalTime time, OccurrenceStatus status, String note, Integer remindMeBeforeDays, boolean reminded, Boolean autoDone) {
		return Occurrence.builder()
			.userId(USER_ID)
			.eventId(eventId)
			.occurrenceId(occurrenceId)
			.date(date)
			.time(time)
			.status(status)
			.note(note)
			.remindMeBeforeDays(remindMeBeforeDays)
			.reminded(reminded)
			.autoDone(autoDone)
			.build();
	}
}
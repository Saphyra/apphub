package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.api.feature.calendar.model.RepetitionType;
import com.github.saphyra.apphub.api.feature.calendar.model.response.EventResponse;
import com.github.saphyra.apphub.api.feature.calendar.model.response.LabelResponse;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.service.LabelQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class EventResponseMapperTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID_1 = UUID.randomUUID();
    private static final UUID EVENT_ID_2 = UUID.randomUUID();
    private static final String REPETITION_DATA_1 = "{\"days\":[\"MONDAY\",\"TUESDAY\"]}";
    private static final String REPETITION_DATA_2 = "{\"xDays\":3}";
    private static final Integer REPEAT_FOR_DAYS = 7;
    private static final LocalDate START_DATE = LocalDate.of(2030, 1, 2);
    private static final LocalDate END_DATE = LocalDate.of(2030, 1, 3);
    private static final LocalTime TIME = LocalTime.of(12, 13);
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final Integer REMIND_ME_BEFORE_DAYS = 2;
    private static final Object PARSED_REPETITION_DATA = "parsed-repetition-data";

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private LabelQueryService labelQueryService;

    @InjectMocks
    private EventResponseMapper underTest;

    @Mock
    private LabelResponse labelResponse;

    @Test
    void toResponse_event() {
        Event event = createEvent(EVENT_ID_1, REPETITION_DATA_1, false);
        given(labelQueryService.getByEventId(USER_ID,EVENT_ID_1)).willReturn(List.of(labelResponse));
        given(objectMapper.readValue(REPETITION_DATA_1, Object.class)).willReturn(PARSED_REPETITION_DATA);

        EventResponse result = underTest.toResponse(event, true);

        assertThat(result)
            .returns(EVENT_ID_1, EventResponse::getEventId)
            .returns(RepetitionType.DAYS_OF_WEEK, EventResponse::getRepetitionType)
            .returns(PARSED_REPETITION_DATA, EventResponse::getRepetitionData)
            .returns(REPEAT_FOR_DAYS, EventResponse::getRepeatForDays)
            .returns(START_DATE, EventResponse::getStartDate)
            .returns(END_DATE, EventResponse::getEndDate)
            .returns(TIME, EventResponse::getTime)
            .returns(TITLE, EventResponse::getTitle)
            .returns(CONTENT, EventResponse::getContent)
            .returns(REMIND_ME_BEFORE_DAYS, EventResponse::getRemindMeBeforeDays)
            .returns(List.of(labelResponse), EventResponse::getLabels)
            .returns(false, EventResponse::getArchived)
            .returns(true, EventResponse::getShared);
    }

    @Test
    void toResponse_events() {
        Event event1 = createEvent(EVENT_ID_1, REPETITION_DATA_1, false);
        Event event2 = createEvent(EVENT_ID_2, REPETITION_DATA_2, true);
        Object parsedRepetitionData1 = List.of("MONDAY", "TUESDAY");
        Object parsedRepetitionData2 = Map.of("xDays", 3);
        given(objectMapper.readValue(REPETITION_DATA_1, Object.class)).willReturn(parsedRepetitionData1);
        given(objectMapper.readValue(REPETITION_DATA_2, Object.class)).willReturn(parsedRepetitionData2);
        given(labelQueryService.getByEventId(USER_ID, EVENT_ID_1)).willReturn(List.of(labelResponse));
        given(labelQueryService.getByEventId(USER_ID, EVENT_ID_2)).willReturn(List.of(labelResponse));

        List<EventResponse> result = underTest.toResponse(List.of(new BiWrapper<>(event1, true), new BiWrapper<>(event2, false)));

        assertThat(result).hasSize(2);
        assertThat(result.get(0))
            .returns(EVENT_ID_1, EventResponse::getEventId)
            .returns(parsedRepetitionData1, EventResponse::getRepetitionData)
            .returns(List.of(labelResponse), EventResponse::getLabels)
            .returns(false, EventResponse::getArchived)
            .returns(true, EventResponse::getShared);
        assertThat(result.get(1))
            .returns(EVENT_ID_2, EventResponse::getEventId)
            .returns(parsedRepetitionData2, EventResponse::getRepetitionData)
            .returns(List.of(labelResponse), EventResponse::getLabels)
            .returns(true, EventResponse::getArchived)
            .returns(false, EventResponse::getShared);
    }

    @Test
    void toResponse_eventWithNullRepetitionData() {
        Event event = createEvent(EVENT_ID_1, null, false);
        given(labelQueryService.getByEventId(USER_ID, EVENT_ID_1)).willReturn(List.of(labelResponse));

        EventResponse result = underTest.toResponse(event, true);

        assertThat(result)
            .returns(EVENT_ID_1, EventResponse::getEventId)
            .returns(null, EventResponse::getRepetitionData)
            .returns(List.of(labelResponse), EventResponse::getLabels)
            .returns(true, EventResponse::getShared);
        then(objectMapper).should(never()).readValue(anyString(), eq(Object.class));
    }

    private Event createEvent(UUID eventId, String repetitionData, boolean archived) {
        return Event.builder()
            .eventId(eventId)
            .userId(USER_ID)
            .repetitionType(RepetitionType.DAYS_OF_WEEK)
            .repetitionData(repetitionData)
            .repeatForDays(REPEAT_FOR_DAYS)
            .startDate(START_DATE)
            .endDate(END_DATE)
            .time(TIME)
            .title(TITLE)
            .content(CONTENT)
            .remindMeBeforeDays(REMIND_ME_BEFORE_DAYS)
            .expirationNotified(false)
            .archived(archived)
            .build();
    }
}
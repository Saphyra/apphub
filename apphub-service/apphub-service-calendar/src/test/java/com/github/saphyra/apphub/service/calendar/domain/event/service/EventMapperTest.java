package com.github.saphyra.apphub.service.calendar.domain.event.service;

import com.github.saphyra.apphub.api.calendar.model.RepetitionType;
import com.github.saphyra.apphub.api.calendar.model.response.EventResponse;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.dao.EventLabelMapping;
import com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EventMapperTest {
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final String REPETITION_DATA = "repetition_data";
    private static final Integer REPEAT_FOR_DAYS = 42;
    private static final LocalDate START_DATE = LocalDate.now();
    private static final LocalDate END_DATE = LocalDate.now().plusDays(1);
    private static final LocalTime TIME = LocalTime.now();
    private static final String CONTENT = "content";
    private static final Integer REMIND_ME_BEFORE_DAYS = 7;
    private static final Object PARSED_REPETITION_DATA = "parsed_repetition_data";
    private static final UUID LABEL_ID = UUID.randomUUID();

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private EventLabelMappingDao eventLabelMappingDao;

    @InjectMocks
    private EventMapper underTest;

    @Mock
    private EventLabelMapping eventLabelMapping;

    @Test
    void toResponse() {
        Event event = Event.builder()
            .eventId(EVENT_ID)
            .title(TITLE)
            .repetitionType(RepetitionType.EVERY_X_DAYS)
            .repetitionData(REPETITION_DATA)
            .repeatForDays(REPEAT_FOR_DAYS)
            .startDate(START_DATE)
            .endDate(END_DATE)
            .time(TIME)
            .content(CONTENT)
            .remindMeBeforeDays(REMIND_ME_BEFORE_DAYS)
            .build();

        given(objectMapper.readValue(REPETITION_DATA, Object.class)).willReturn(PARSED_REPETITION_DATA);
        given(eventLabelMappingDao.getByEventId(EVENT_ID)).willReturn(List.of(eventLabelMapping));
        given(eventLabelMapping.getLabelId()).willReturn(LABEL_ID);

        assertThat(underTest.toResponse(event))
            .returns(EVENT_ID, EventResponse::getEventId)
            .returns(TITLE, EventResponse::getTitle)
            .returns(RepetitionType.EVERY_X_DAYS, EventResponse::getRepetitionType)
            .returns(PARSED_REPETITION_DATA, EventResponse::getRepetitionData)
            .returns(REPEAT_FOR_DAYS, EventResponse::getRepeatForDays)
            .returns(START_DATE, EventResponse::getStartDate)
            .returns(END_DATE, EventResponse::getEndDate)
            .returns(TIME, EventResponse::getTime)
            .returns(CONTENT, EventResponse::getContent)
            .returns(REMIND_ME_BEFORE_DAYS, EventResponse::getRemindMeBeforeDays)
            .returns(List.of(LABEL_ID), EventResponse::getLabels);
    }
}
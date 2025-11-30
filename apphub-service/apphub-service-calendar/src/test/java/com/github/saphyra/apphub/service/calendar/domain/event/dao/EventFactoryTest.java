package com.github.saphyra.apphub.service.calendar.domain.event.dao;

import com.github.saphyra.apphub.api.calendar.model.RepetitionType;
import com.github.saphyra.apphub.api.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EventFactoryTest {
    private static final Integer REPEAT_FOR_DAYS = 123;
    private static final LocalDate START_DATE = LocalDate.now();
    private static final LocalDate END_DATE = LocalDate.now();
    private static final LocalTime TIME = LocalTime.now();
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final Integer REMIND_ME_BEFORE_DAYS = 321;
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final String STRINGIFIED_REPETITION_DATA = "stringified_repetition_data";
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private EventFactory underTest;

    @Test
    void create() {
        EventRequest request = EventRequest.builder()
            .repetitionType(RepetitionType.EVERY_X_DAYS)
            .repeatForDays(REPEAT_FOR_DAYS)
            .startDate(START_DATE)
            .endDate(END_DATE)
            .time(TIME)
            .title(TITLE)
            .content(CONTENT)
            .remindMeBeforeDays(REMIND_ME_BEFORE_DAYS)
            .build();
        given(idGenerator.randomUuid()).willReturn(EVENT_ID);
        given(objectMapper.writeValueAsString(request.getRepetitionData())).willReturn(STRINGIFIED_REPETITION_DATA);

        assertThat(underTest.create(USER_ID, request))
            .returns(EVENT_ID, Event::getEventId)
            .returns(USER_ID, Event::getUserId)
            .returns(RepetitionType.EVERY_X_DAYS, Event::getRepetitionType)
            .returns(STRINGIFIED_REPETITION_DATA, Event::getRepetitionData)
            .returns(REPEAT_FOR_DAYS, Event::getRepeatForDays)
            .returns(START_DATE, Event::getStartDate)
            .returns(END_DATE, Event::getEndDate)
            .returns(TIME, Event::getTime)
            .returns(TITLE, Event::getTitle)
            .returns(CONTENT, Event::getContent)
            .returns(REMIND_ME_BEFORE_DAYS, Event::getRemindMeBeforeDays);
    }

}
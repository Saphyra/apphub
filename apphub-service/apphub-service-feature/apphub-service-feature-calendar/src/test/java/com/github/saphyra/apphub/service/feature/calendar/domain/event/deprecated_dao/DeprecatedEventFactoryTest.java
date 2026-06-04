package com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao;

import com.github.saphyra.apphub.api.feature.calendar.model.RepetitionType;
import com.github.saphyra.apphub.api.feature.calendar.model.request.EventRequest;
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
@Deprecated(forRemoval = true)
class DeprecatedEventFactoryTest {
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
    private DeprecatedEventFactory underTest;

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
            .returns(EVENT_ID, DeprecatedEvent::getEventId)
            .returns(USER_ID, DeprecatedEvent::getUserId)
            .returns(RepetitionType.EVERY_X_DAYS, DeprecatedEvent::getRepetitionType)
            .returns(STRINGIFIED_REPETITION_DATA, DeprecatedEvent::getRepetitionData)
            .returns(REPEAT_FOR_DAYS, DeprecatedEvent::getRepeatForDays)
            .returns(START_DATE, DeprecatedEvent::getStartDate)
            .returns(END_DATE, DeprecatedEvent::getEndDate)
            .returns(TIME, DeprecatedEvent::getTime)
            .returns(TITLE, DeprecatedEvent::getTitle)
            .returns(CONTENT, DeprecatedEvent::getContent)
            .returns(REMIND_ME_BEFORE_DAYS, DeprecatedEvent::getRemindMeBeforeDays);
    }

}
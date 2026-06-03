package com.github.saphyra.apphub.service.feature.calendar.domain.event.dao;

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
class EventFactoryTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final RepetitionType REPETITION_TYPE = RepetitionType.DAYS_OF_WEEK;
    private static final String REPETITION_DATA_JSON = "{\"days\":[\"MONDAY\",\"WEDNESDAY\"]}";
    private static final Integer REPEAT_FOR_DAYS = 10;
    private static final LocalDate START_DATE = LocalDate.of(2026, 6, 3);
    private static final LocalDate END_DATE = LocalDate.of(2026, 7, 3);
    private static final LocalTime TIME = LocalTime.of(14, 30);
    private static final String TITLE = "Test Event";
    private static final String CONTENT = "Event Content";
    private static final Integer REMIND_ME_BEFORE_DAYS = 2;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private EventFactory underTest;

    @Test
    public void testCreate() {
        Object repetitionData = new Object();
        EventRequest request = EventRequest.builder()
            .repetitionType(REPETITION_TYPE)
            .repetitionData(repetitionData)
            .repeatForDays(REPEAT_FOR_DAYS)
            .startDate(START_DATE)
            .endDate(END_DATE)
            .time(TIME)
            .title(TITLE)
            .content(CONTENT)
            .remindMeBeforeDays(REMIND_ME_BEFORE_DAYS)
            .build();

        given(idGenerator.randomUuid()).willReturn(EVENT_ID);
        given(objectMapper.writeValueAsString(repetitionData)).willReturn(REPETITION_DATA_JSON);

        Event result = underTest.create(USER_ID, request);

        assertThat(result.getEventId()).isEqualTo(EVENT_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getRepetitionType()).isEqualTo(REPETITION_TYPE);
        assertThat(result.getRepetitionData()).isEqualTo(REPETITION_DATA_JSON);
        assertThat(result.getRepeatForDays()).isEqualTo(REPEAT_FOR_DAYS);
        assertThat(result.getStartDate()).isEqualTo(START_DATE);
        assertThat(result.getEndDate()).isEqualTo(END_DATE);
        assertThat(result.getTime()).isEqualTo(TIME);
        assertThat(result.getTitle()).isEqualTo(TITLE);
        assertThat(result.getContent()).isEqualTo(CONTENT);
        assertThat(result.getRemindMeBeforeDays()).isEqualTo(REMIND_ME_BEFORE_DAYS);
    }
}
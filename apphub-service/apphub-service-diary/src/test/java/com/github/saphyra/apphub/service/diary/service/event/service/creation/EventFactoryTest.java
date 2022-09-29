package com.github.saphyra.apphub.service.diary.service.event.service.creation;

import com.github.saphyra.apphub.api.diary.model.CreateEventRequest;
import com.github.saphyra.apphub.api.diary.model.RepetitionType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.diary.dao.event.Event;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class EventFactoryTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final LocalDate START_DATE = LocalDate.now();
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String REPETITION_DATA = "repetition-data";
    private static final Integer REPETITION_TYPE_DAYS = 25;
    private static final Integer HOURS = 21;
    private static final Integer MINUTES = 24;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @InjectMocks
    private EventFactory underTest;

    @Test
    public void createOneTime() {
        CreateEventRequest request = CreateEventRequest.builder()
            .repetitionType(RepetitionType.ONE_TIME)
            .date(START_DATE)
            .hours(HOURS)
            .minutes(MINUTES)
            .title(TITLE)
            .content(CONTENT)
            .build();

        given(idGenerator.randomUuid()).willReturn(EVENT_ID);

        Event event = underTest.create(USER_ID, request);

        assertThat(event.getEventId()).isEqualTo(EVENT_ID);
        assertThat(event.getUserId()).isEqualTo(USER_ID);
        assertThat(event.getStartDate()).isEqualTo(START_DATE);
        assertThat(event.getRepetitionType()).isEqualTo(RepetitionType.ONE_TIME);
        assertThat(event.getTitle()).isEqualTo(TITLE);
        assertThat(event.getContent()).isEqualTo(CONTENT);
        assertThat(event.getRepetitionData()).isNull();
        assertThat(event.getTime()).isEqualTo(LocalTime.of(HOURS, MINUTES, 0));
    }

    @Test
    public void createDaysOfWeek() {
        CreateEventRequest request = CreateEventRequest.builder()
            .repetitionType(RepetitionType.DAYS_OF_WEEK)
            .repetitionDaysOfWeek(List.of(DayOfWeek.MONDAY))
            .date(START_DATE)
            .title(TITLE)
            .content(CONTENT)
            .build();

        given(idGenerator.randomUuid()).willReturn(EVENT_ID);
        given(objectMapperWrapper.writeValueAsString(List.of(DayOfWeek.MONDAY))).willReturn(REPETITION_DATA);

        Event event = underTest.create(USER_ID, request);

        assertThat(event.getEventId()).isEqualTo(EVENT_ID);
        assertThat(event.getUserId()).isEqualTo(USER_ID);
        assertThat(event.getStartDate()).isEqualTo(START_DATE);
        assertThat(event.getRepetitionType()).isEqualTo(RepetitionType.DAYS_OF_WEEK);
        assertThat(event.getTitle()).isEqualTo(TITLE);
        assertThat(event.getContent()).isEqualTo(CONTENT);
        assertThat(event.getRepetitionData()).isEqualTo(REPETITION_DATA);
        assertThat(event.getTime()).isNull();
    }

    @Test
    public void createDaysOfMonth() {
        CreateEventRequest request = CreateEventRequest.builder()
            .repetitionType(RepetitionType.DAYS_OF_MONTH)
            .repetitionDaysOfMonth(List.of(1))
            .date(START_DATE)
            .title(TITLE)
            .content(CONTENT)
            .build();

        given(idGenerator.randomUuid()).willReturn(EVENT_ID);
        given(objectMapperWrapper.writeValueAsString(List.of(1))).willReturn(REPETITION_DATA);

        Event event = underTest.create(USER_ID, request);

        assertThat(event.getEventId()).isEqualTo(EVENT_ID);
        assertThat(event.getUserId()).isEqualTo(USER_ID);
        assertThat(event.getStartDate()).isEqualTo(START_DATE);
        assertThat(event.getRepetitionType()).isEqualTo(RepetitionType.DAYS_OF_MONTH);
        assertThat(event.getTitle()).isEqualTo(TITLE);
        assertThat(event.getContent()).isEqualTo(CONTENT);
        assertThat(event.getRepetitionData()).isEqualTo(REPETITION_DATA);
    }

    @Test
    public void createEveryXDays() {
        CreateEventRequest request = CreateEventRequest.builder()
            .repetitionType(RepetitionType.EVERY_X_DAYS)
            .repetitionDays(REPETITION_TYPE_DAYS)
            .date(START_DATE)
            .title(TITLE)
            .content(CONTENT)
            .build();

        given(idGenerator.randomUuid()).willReturn(EVENT_ID);

        Event event = underTest.create(USER_ID, request);

        assertThat(event.getEventId()).isEqualTo(EVENT_ID);
        assertThat(event.getUserId()).isEqualTo(USER_ID);
        assertThat(event.getStartDate()).isEqualTo(START_DATE);
        assertThat(event.getRepetitionType()).isEqualTo(RepetitionType.EVERY_X_DAYS);
        assertThat(event.getTitle()).isEqualTo(TITLE);
        assertThat(event.getContent()).isEqualTo(CONTENT);
        assertThat(event.getRepetitionData()).isEqualTo(REPETITION_TYPE_DAYS.toString());
    }
}
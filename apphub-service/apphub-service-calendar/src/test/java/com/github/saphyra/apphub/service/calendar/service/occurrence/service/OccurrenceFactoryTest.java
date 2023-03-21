package com.github.saphyra.apphub.service.calendar.service.occurrence.service;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.calendar.dao.event.Event;
import com.github.saphyra.apphub.service.calendar.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.calendar.dao.occurance.OccurrenceStatus;
import com.github.saphyra.apphub.service.calendar.dao.occurance.OccurrenceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class OccurrenceFactoryTest {
    private static final LocalDate DATE = LocalDate.now();
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final LocalTime TIME = LocalTime.now();

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private OccurrenceFactory underTest;

    @Mock
    private Event event;

    @BeforeEach
    public void setUp() {
        given(idGenerator.randomUuid()).willReturn(OCCURRENCE_ID);
    }

    @Test
    public void createPending() {
        given(event.getStartDate()).willReturn(DATE);
        given(event.getEventId()).willReturn(EVENT_ID);
        given(event.getUserId()).willReturn(USER_ID);
        given(event.getTime()).willReturn(TIME);

        Occurrence result = underTest.createPending(event);

        assertThat(result.getOccurrenceId()).isEqualTo(OCCURRENCE_ID);
        assertThat(result.getEventId()).isEqualTo(EVENT_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getDate()).isEqualTo(DATE);
        assertThat(result.getTime()).isEqualTo(TIME);
        assertThat(result.getStatus()).isEqualTo(OccurrenceStatus.PENDING);
        assertThat(result.getNote()).isNull();
    }

    @Test
    public void createVirtual() {
        given(event.getEventId()).willReturn(EVENT_ID);
        given(event.getUserId()).willReturn(USER_ID);

        Occurrence result = underTest.createVirtual(DATE, event, OccurrenceType.DEFAULT);

        assertThat(result.getOccurrenceId()).isEqualTo(OCCURRENCE_ID);
        assertThat(result.getEventId()).isEqualTo(EVENT_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getDate()).isEqualTo(DATE);
        assertThat(result.getStatus()).isEqualTo(OccurrenceStatus.VIRTUAL);
        assertThat(result.getNote()).isNull();
        assertThat(result.getType()).isEqualTo(OccurrenceType.DEFAULT);
    }
}
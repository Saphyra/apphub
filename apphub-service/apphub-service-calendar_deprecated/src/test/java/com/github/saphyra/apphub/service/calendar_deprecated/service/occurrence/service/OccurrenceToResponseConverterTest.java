package com.github.saphyra.apphub.service.calendar_deprecated.service.occurrence.service;

import com.github.saphyra.apphub.api.calendar.model.OccurrenceResponse;
import com.github.saphyra.apphub.service.calendar_deprecated.dao.event.Event;
import com.github.saphyra.apphub.service.calendar_deprecated.dao.event.EventDao;
import com.github.saphyra.apphub.service.calendar_deprecated.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.calendar_deprecated.dao.occurance.OccurrenceStatus;
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
public class OccurrenceToResponseConverterTest {
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String NOTE = "note";
    private static final LocalDate DATE = LocalDate.now();
    private static final LocalTime TIME = LocalTime.now();

    @Mock
    private EventDao eventDao;

    @InjectMocks
    private OccurrenceToResponseConverter underTest;

    @Mock
    private Occurrence occurrence;

    @Mock
    private Event event;

    @Test
    public void convert() {
        given(occurrence.getOccurrenceId()).willReturn(OCCURRENCE_ID);
        given(occurrence.getEventId()).willReturn(EVENT_ID);
        given(occurrence.getStatus()).willReturn(OccurrenceStatus.PENDING);
        given(occurrence.getNote()).willReturn(NOTE);
        given(occurrence.getDate()).willReturn(DATE);
        given(occurrence.getTime()).willReturn(TIME);

        given(eventDao.findByIdValidated(EVENT_ID)).willReturn(event);
        given(event.getTitle()).willReturn(TITLE);
        given(event.getContent()).willReturn(CONTENT);

        OccurrenceResponse result = underTest.convert(occurrence);

        assertThat(result.getOccurrenceId()).isEqualTo(OCCURRENCE_ID);
        assertThat(result.getEventId()).isEqualTo(EVENT_ID);
        assertThat(result.getStatus()).isEqualTo(OccurrenceStatus.PENDING.name());
        assertThat(result.getTitle()).isEqualTo(TITLE);
        assertThat(result.getContent()).isEqualTo(CONTENT);
        assertThat(result.getNote()).isEqualTo(NOTE);
        assertThat(result.getDate()).isEqualTo(DATE);
        assertThat(result.getTime()).isEqualTo(TIME);
    }
}
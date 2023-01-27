package com.github.saphyra.apphub.service.diary.service.occurrence;

import com.github.saphyra.apphub.api.diary.model.CalendarResponse;
import com.github.saphyra.apphub.api.diary.model.EditOccurrenceRequest;
import com.github.saphyra.apphub.api.diary.model.ReferenceDate;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.diary.service.occurrence.service.MarkOccurrenceDefaultService;
import com.github.saphyra.apphub.service.diary.service.occurrence.service.MarkOccurrenceDoneService;
import com.github.saphyra.apphub.service.diary.service.occurrence.service.MarkOccurrenceSnoozedService;
import com.github.saphyra.apphub.service.diary.service.occurrence.service.edit.EditOccurrenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class OccurrenceControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();

    @Mock
    private EditOccurrenceService editOccurrenceService;

    @Mock
    private MarkOccurrenceDoneService markOccurrenceDoneService;

    @Mock
    private MarkOccurrenceSnoozedService markOccurrenceSnoozedService;

    @Mock
    private MarkOccurrenceDefaultService markOccurrenceDefaultService;

    @InjectMocks
    private OccurrenceControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private EditOccurrenceRequest editOccurrenceRequest;

    @Mock
    private CalendarResponse calendarResponse;

    @Mock
    private ReferenceDate referenceDate;

    @BeforeEach
    public void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void editOccurrence() {
        given(editOccurrenceService.edit(USER_ID, OCCURRENCE_ID, editOccurrenceRequest)).willReturn(List.of(calendarResponse));

        List<CalendarResponse> result = underTest.editOccurrence(editOccurrenceRequest, OCCURRENCE_ID, accessTokenHeader);

        assertThat(result).containsExactly(calendarResponse);
    }

    @Test
    public void markOccurrenceDone() {
        given(markOccurrenceDoneService.markDone(USER_ID, OCCURRENCE_ID, referenceDate)).willReturn(List.of(calendarResponse));

        List<CalendarResponse> result = underTest.markOccurrenceDone(referenceDate, OCCURRENCE_ID, accessTokenHeader);

        assertThat(result).containsExactly(calendarResponse);
    }

    @Test
    public void markOccurrenceSnoozed() {
        given(markOccurrenceSnoozedService.markSnoozed(USER_ID, OCCURRENCE_ID, referenceDate)).willReturn(List.of(calendarResponse));

        List<CalendarResponse> result = underTest.markOccurrenceSnoozed(referenceDate, OCCURRENCE_ID, accessTokenHeader);

        assertThat(result).containsExactly(calendarResponse);
    }

    @Test
    public void markOccurrenceDefault() {
        given(markOccurrenceDefaultService.markDefault(USER_ID, OCCURRENCE_ID, referenceDate)).willReturn(List.of(calendarResponse));

        List<CalendarResponse> result = underTest.markOccurrenceDefault(referenceDate, OCCURRENCE_ID, accessTokenHeader);

        assertThat(result).containsExactly(calendarResponse);
    }
}
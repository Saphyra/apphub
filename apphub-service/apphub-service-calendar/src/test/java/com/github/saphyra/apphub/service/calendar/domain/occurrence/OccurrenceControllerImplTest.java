package com.github.saphyra.apphub.service.calendar.domain.occurrence;

import com.github.saphyra.apphub.api.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.api.calendar.model.request.OccurrenceRequest;
import com.github.saphyra.apphub.api.calendar.model.response.OccurrenceResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.service.CreateOccurrenceService;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.service.DeleteOccurrenceService;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.service.EditOccurrenceService;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.service.OccurrenceQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class OccurrenceControllerImplTest {
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();
    private static final LocalDate START_DATE = LocalDate.now().minusDays(1);
    private static final LocalDate END_DATE = LocalDate.now().plusDays(1);
    private static final UUID LABEL_ID = UUID.randomUUID();

    @Mock
    private EditOccurrenceService editOccurrenceService;

    @Mock
    private CreateOccurrenceService createOccurrenceService;

    @Mock
    private OccurrenceQueryService occurrenceQueryService;

    @Mock
    private DeleteOccurrenceService deleteOccurrenceService;

    @InjectMocks
    private OccurrenceControllerImpl underTest;

    @Mock
    private OccurrenceRequest occurrenceRequest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private OccurrenceResponse occurrenceResponse;

    @Test
    void createOccurrence() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);

        underTest.createOccurrence(occurrenceRequest, EVENT_ID, accessTokenHeader);

        then(createOccurrenceService).should().createOccurrence(USER_ID, EVENT_ID, occurrenceRequest);
    }

    @Test
    void editOccurrence() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);

        underTest.editOccurrence(occurrenceRequest, OCCURRENCE_ID, accessTokenHeader);

        then(editOccurrenceService).should().editOccurrence(OCCURRENCE_ID, occurrenceRequest);
    }

    @Test
    void deleteOccurrence() {
        underTest.deleteOccurrence(OCCURRENCE_ID, accessTokenHeader);

        then(deleteOccurrenceService).should().deleteOccurrence(OCCURRENCE_ID);
    }

    @Test
    void getOccurrences() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(occurrenceQueryService.getOccurrences(USER_ID, START_DATE, END_DATE, LABEL_ID)).willReturn(List.of(occurrenceResponse));

        assertThat(underTest.getOccurrences(START_DATE, END_DATE, LABEL_ID, accessTokenHeader)).containsExactly(occurrenceResponse);
    }

    @Test
    void getOccurrence() {
        given(occurrenceQueryService.getOccurrence(OCCURRENCE_ID)).willReturn(occurrenceResponse);

        assertThat(underTest.getOccurrence(OCCURRENCE_ID, accessTokenHeader)).isEqualTo(occurrenceResponse);
    }

    @Test
    void getOccurrencesOfEvent() {
        given(occurrenceQueryService.getOccurrencesOfEvent(EVENT_ID)).willReturn(List.of(occurrenceResponse));

        assertThat(underTest.getOccurrencesOfEvent(EVENT_ID, accessTokenHeader)).containsExactly(occurrenceResponse);
    }

    @Test
    void editOccurrenceStatus() {
        given(editOccurrenceService.editOccurrenceStatus(OCCURRENCE_ID, OccurrenceStatus.DONE)).willReturn(occurrenceResponse);

        assertThat(underTest.editOccurrenceStatus(new OneParamRequest<>(OccurrenceStatus.DONE), OCCURRENCE_ID, accessTokenHeader)).isEqualTo(occurrenceResponse);
    }

    @Test
    void setReminded() {
        given(editOccurrenceService.setReminded(OCCURRENCE_ID)).willReturn(occurrenceResponse);

        assertThat(underTest.setReminded(OCCURRENCE_ID, accessTokenHeader)).isEqualTo(occurrenceResponse);
    }
}
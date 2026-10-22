package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.api.feature.calendar.model.response.EventResponse;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.service.object_query.EventObjectQueryService;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.object_query.OccurrenceObjectQueryService;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class SearchEventServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID_1 = UUID.randomUUID();
    private static final String SEARCH = "search";

    @Mock
    private EventObjectQueryService eventObjectQueryService;

    @Mock
    private OccurrenceObjectQueryService occurrenceObjectQueryService;

    @Mock
    private EventResponseMapper eventResponseMapper;

    @InjectMocks
    private SearchEventService underTest;

    @Mock
    private Event event1;

    @Mock
    private Occurrence occurrence;

    @Mock
    private EventResponse eventResponse;

    @Test
    void search_tooShortSearchText() {
        Throwable ex = catchThrowable(() -> underTest.search(USER_ID, "ab"));

        ExceptionValidator.validateInvalidParam(ex, "searchText", ValidationUtil.TOO_SHORT);
        then(eventObjectQueryService).shouldHaveNoInteractions();
        then(occurrenceObjectQueryService).shouldHaveNoInteractions();
        then(eventResponseMapper).shouldHaveNoInteractions();
    }

    @Test
    void search_eventMatchesByTitle() {
        given(eventObjectQueryService.getEvents(USER_ID)).willReturn(List.of(event1));
        given(event1.getTitle()).willReturn("My Search Text");
        given(eventResponseMapper.toResponse(USER_ID, List.of(event1))).willReturn(List.of(eventResponse));

        List<EventResponse> result = underTest.search(USER_ID, SEARCH);

        assertThat(result).containsExactly(eventResponse);
    }

    @Test
    void search_eventMatchesByOccurrenceNote() {
        given(eventObjectQueryService.getEvents(USER_ID)).willReturn(List.of(event1));
        given(event1.getEventId()).willReturn(EVENT_ID_1);
        given(event1.getTitle()).willReturn("title");
        given(event1.getContent()).willReturn("content");
        given(occurrenceObjectQueryService.getOccurrences(USER_ID, EVENT_ID_1)).willReturn(new BiWrapper<>(event1, List.of(occurrence)));
        given(occurrence.getNote()).willReturn("contains SeaRCh here");
        given(eventResponseMapper.toResponse(USER_ID, List.of(event1))).willReturn(List.of(eventResponse));

        List<EventResponse> result = underTest.search(USER_ID, SEARCH);

        assertThat(result).containsExactly(eventResponse);
    }

    @Test
    void search_sharedOccurrenceMatches() {
        given(eventObjectQueryService.getEvents(USER_ID)).willReturn(List.of());
        given(occurrenceObjectQueryService.getOccurrences(USER_ID)).willReturn(Map.of(event1, List.of(occurrence)));
        given(event1.getEventId()).willReturn(EVENT_ID_1);
        given(occurrence.getNote()).willReturn("contains SeaRCh here");
        given(eventResponseMapper.toResponse(USER_ID, List.of(event1))).willReturn(List.of(eventResponse));

        List<EventResponse> result = underTest.search(USER_ID, SEARCH);

        assertThat(result).containsExactly(eventResponse);
    }

    @Test
    void search_sharedOccurrenceAlreadyInEventList() {
        given(eventObjectQueryService.getEvents(USER_ID)).willReturn(List.of(event1));
        given(event1.getEventId()).willReturn(EVENT_ID_1);
        given(event1.getTitle()).willReturn(SEARCH);
        given(occurrenceObjectQueryService.getOccurrences(USER_ID)).willReturn(Map.of(event1, List.of(occurrence)));
        given(eventResponseMapper.toResponse(USER_ID, List.of(event1))).willReturn(List.of(eventResponse));

        List<EventResponse> result = underTest.search(USER_ID, SEARCH);

        assertThat(result).containsExactly(eventResponse);
    }
}
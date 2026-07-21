package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.api.feature.calendar.model.response.EventResponse;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class SearchEventServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID_1 = UUID.randomUUID();
    private static final UUID EVENT_ID_2 = UUID.randomUUID();
    private static final String SEARCH = "search";

    @Mock
    private EventDao eventDao;

    @Mock
    private OccurrenceDao occurrenceDao;

    @Mock
    private EventResponseMapper eventResponseMapper;

    @InjectMocks
    private SearchEventService underTest;

    @Mock
    private Event event1;

    @Mock
    private Event event2;

    @Mock
    private Occurrence occurrence;

    @Mock
    private EventResponse eventResponse;

    @Test
    void search_tooShortSearchText() {
        Throwable ex = catchThrowable(() -> underTest.search(USER_ID, "ab"));

        ExceptionValidator.validateInvalidParam(ex, "searchText", "too short");
        then(eventDao).shouldHaveNoInteractions();
        then(eventResponseMapper).shouldHaveNoInteractions();
    }

    @Test
    void search_eventMatchesByTitle() {
        given(eventDao.getByUserId(USER_ID)).willReturn(List.of(event1));
        given(event1.getTitle()).willReturn("My Search Text");
        given(eventResponseMapper.toResponse(USER_ID, List.of(new BiWrapper<>(event1, false)))).willReturn(List.of(eventResponse));

        List<EventResponse> result = underTest.search(USER_ID, SEARCH);

        assertThat(result).containsExactly(eventResponse);
        then(occurrenceDao).shouldHaveNoInteractions();
    }

    @Test
    void search_eventMatchesByOccurrenceNote() {
        given(eventDao.getByUserId(USER_ID)).willReturn(List.of(event1));
        given(event1.getEventId()).willReturn(EVENT_ID_1);
        given(event1.getTitle()).willReturn("title");
        given(event1.getContent()).willReturn("content");
        given(occurrenceDao.getByEventId(EVENT_ID_1)).willReturn(List.of(occurrence));
        given(occurrence.getNote()).willReturn("contains SeaRCh here");
        given(eventResponseMapper.toResponse(USER_ID, List.of(new BiWrapper<>(event1, false)))).willReturn(List.of(eventResponse));

        List<EventResponse> result = underTest.search(USER_ID, SEARCH);

        assertThat(result).containsExactly(eventResponse);
    }
}
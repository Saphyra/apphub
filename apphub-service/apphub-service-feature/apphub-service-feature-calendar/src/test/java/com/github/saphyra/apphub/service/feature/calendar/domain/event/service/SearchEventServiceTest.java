package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.api.feature.calendar.model.response.EventResponse;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEvent;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.deprecated_dao.DeprecatedOccurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.deprecated_dao.DeprecatedOccurrenceDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SearchEventServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String SEARCH = "search";
    private static final UUID EVENT_ID_1 = UUID.randomUUID();
    private static final UUID EVENT_ID_2 = UUID.randomUUID();
    private static final UUID EVENT_ID_3 = UUID.randomUUID();
    private static final UUID EVENT_ID_4 = UUID.randomUUID();

    @Mock
    private DeprecatedEventDao eventDao;

    @Mock
    private DeprecatedOccurrenceDao occurrenceDao;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private SearchEventService underTest;

    @Mock
    private DeprecatedEvent eventWithMatchingTitle;

    @Mock
    private DeprecatedEvent eventWithMatchingContent;

    @Mock
    private DeprecatedEvent unmatchedEvent;

    @Mock
    private DeprecatedOccurrence occurrenceWithMatchingNote;

    @Mock
    private DeprecatedOccurrence unmatchingOccurrence;

    @Mock
    private DeprecatedEvent occurrenceEvent;

    @Mock
    private EventResponse eventResponse;

    @Test
    void searchTextTooShort() {
        ExceptionValidator.validateInvalidParam(() -> underTest.search(USER_ID, "aa"), "searchText", "too short");
    }

    @Test
    void search() {
        given(eventDao.getByUserId(USER_ID)).willReturn(List.of(eventWithMatchingTitle, eventWithMatchingContent, unmatchedEvent, occurrenceEvent));
        given(occurrenceDao.getByUserId(USER_ID)).willReturn(List.of(occurrenceWithMatchingNote, unmatchingOccurrence));
        given(eventWithMatchingTitle.getTitle()).willReturn(SEARCH);
        given(eventWithMatchingContent.getTitle()).willReturn("asdf");
        given(eventWithMatchingContent.getContent()).willReturn(SEARCH);
        given(unmatchedEvent.getTitle()).willReturn("asdf");
        given(unmatchedEvent.getContent()).willReturn("asdf");
        given(occurrenceWithMatchingNote.getNote()).willReturn(SEARCH);
        given(unmatchingOccurrence.getNote()).willReturn("asdf");
        given(occurrenceEvent.getTitle()).willReturn("asd");
        given(occurrenceEvent.getContent()).willReturn("asd");
        given(eventWithMatchingTitle.getEventId()).willReturn(EVENT_ID_1);
        given(eventWithMatchingContent.getEventId()).willReturn(EVENT_ID_2);
        given(occurrenceEvent.getEventId()).willReturn(EVENT_ID_3);
        given(unmatchedEvent.getEventId()).willReturn(EVENT_ID_4);
        given(occurrenceWithMatchingNote.getEventId()).willReturn(EVENT_ID_3);
        given(eventMapper.toResponse(any())).willReturn(eventResponse);

        assertThat(underTest.search(USER_ID, SEARCH)).hasSize(3);
    }
}
package com.github.saphyra.apphub.service.feature.calendar.domain.event;

import com.github.saphyra.apphub.api.feature.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.api.feature.calendar.model.response.EventResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.service.ArchiveEventService;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.service.CreateEventService;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.service.DeleteEventService;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.service.EditEventService;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.service.EventQueryService;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.service.ExpiredEventService;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.service.MergeEventService;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.service.SearchEventService;
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
class EventControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LABEL = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final LocalDate EXTEND_UNTIL = LocalDate.now();
    private static final String SEARCH_TEXT = "search-text";

    @Mock
    private CreateEventService createEventService;

    @Mock
    private EventQueryService eventQueryService;

    @Mock
    private DeleteEventService deleteEventService;

    @Mock
    private EditEventService editEventService;

    @Mock
    private ExpiredEventService expiredEventService;

    @Mock
    private MergeEventService mergeEventService;

    @Mock
    private SearchEventService searchEventService;

    @Mock
    private ArchiveEventService archiveEventService;

    @InjectMocks
    private EventControllerImpl underTest;

    @Mock
    private AccessToken accessToken;

    @Mock
    private EventRequest request;

    @Mock
    private EventResponse eventResponse;

    @Test
    void createEvent() {
        given(accessToken.getUserId()).willReturn(USER_ID);
        given(createEventService.create(USER_ID, request)).willReturn(EVENT_ID);

        assertThat(underTest.createEvent(request, accessToken).getValue()).isEqualTo(EVENT_ID);
    }

    @Test
    void getEvents() {
        given(accessToken.getUserId()).willReturn(USER_ID);
        given(eventQueryService.getEvents(USER_ID, LABEL)).willReturn(List.of(eventResponse));

        assertThat(underTest.getEvents(LABEL, accessToken)).containsExactly(eventResponse);
    }

    @Test
    void getEvent() {
        given(accessToken.getUserId()).willReturn(USER_ID);
        given(eventQueryService.getEvent(USER_ID, EVENT_ID)).willReturn(eventResponse);

        assertThat(underTest.getEvent(EVENT_ID, accessToken)).isEqualTo(eventResponse);
    }

    @Test
    void getLabellessEvents() {
        given(accessToken.getUserId()).willReturn(USER_ID);
        given(eventQueryService.getLabellessEvents(USER_ID)).willReturn(List.of(eventResponse));

        assertThat(underTest.getLabellessEvents(accessToken)).containsExactly(eventResponse);
    }

    @Test
    void deleteEvent() {
        given(accessToken.getUserId()).willReturn(USER_ID);

        underTest.deleteEvent(EVENT_ID, accessToken);

        then(deleteEventService).should().delete(USER_ID, EVENT_ID);
    }

    @Test
    void editEvent() {
        given(accessToken.getUserId()).willReturn(USER_ID);

        underTest.editEvent(request, EVENT_ID, accessToken);

        then(editEventService).should().edit(USER_ID, EVENT_ID, request);
    }

    @Test
    void getExpiredEvents() {
        given(accessToken.getUserId()).willReturn(USER_ID);
        given(expiredEventService.getExpiredEvents(USER_ID)).willReturn(List.of(eventResponse));

        assertThat(underTest.getExpiredEvents(accessToken)).containsExactly(eventResponse);
    }

    @Test
    void snoozeEvent() {
        given(accessToken.getUserId()).willReturn(USER_ID);

        underTest.hideExpiredEvent(EVENT_ID, accessToken);

        then(expiredEventService).should().hide(USER_ID, EVENT_ID);
    }

    @Test
    void extendExpiredEvent() {
        given(accessToken.getUserId()).willReturn(USER_ID);

        underTest.extendExpiredEvent(new OneParamRequest<>(EXTEND_UNTIL), EVENT_ID, accessToken);

        then(expiredEventService).should().extend(USER_ID, EVENT_ID, EXTEND_UNTIL);
    }

    @Test
    void mergeEvent() {
        given(accessToken.getUserId()).willReturn(USER_ID);

        underTest.mergeEvents(EVENT_ID, accessToken);

        then(mergeEventService).should().merge(USER_ID, EVENT_ID);
    }

    @Test
    void search() {
        given(accessToken.getUserId()).willReturn(USER_ID);
        given(searchEventService.search(USER_ID, SEARCH_TEXT)).willReturn(List.of(eventResponse));

        assertThat(underTest.searchEvents(new OneParamRequest<>(SEARCH_TEXT), accessToken)).containsExactly(eventResponse);
    }

    @Test
    void archiveEvent(){
        given(accessToken.getUserId()).willReturn(USER_ID);

        underTest.archiveEvent(new OneParamRequest<>(true), EVENT_ID, accessToken);

        then(archiveEventService).should().archive(USER_ID, EVENT_ID, true);
    }
}
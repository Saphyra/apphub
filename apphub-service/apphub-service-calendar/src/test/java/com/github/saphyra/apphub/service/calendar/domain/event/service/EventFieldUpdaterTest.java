package com.github.saphyra.apphub.service.calendar.domain.event.service;

import com.github.saphyra.apphub.api.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.service.calendar.common.context.UpdateEventContext;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Function;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EventFieldUpdaterTest {
    private static final Object VALUE_1 = "value_1";
    private static final Object VALUE_2 = "value_2";

    @Mock
    private Function<EventRequest, Object> requestMapper;

    @Mock
    private Function<Event, Object> eventMapper;

    @Mock
    private Updater updater;

    private EventFieldUpdater underTest;

    @Mock
    private EventRequest request;

    @Mock
    private Event event;

    @Mock
    private UpdateEventContext context;

    @BeforeEach
    void setUp() {
        underTest = TestEventFieldUpdater.builder()
            .requestMapper(requestMapper)
            .eventMapper(eventMapper)
            .updater(updater)
            .build();
    }

    @Test
    void update_equals() {
        given(requestMapper.apply(request)).willReturn(VALUE_1);
        given(eventMapper.apply(event)).willReturn(VALUE_1);

        underTest.update(context, request, event);

        then(updater).shouldHaveNoInteractions();
    }

    @Test
    void update() {
        given(requestMapper.apply(request)).willReturn(VALUE_1);
        given(eventMapper.apply(event)).willReturn(VALUE_2);

        underTest.update(context, request, event);

        then(updater).should().update(context, request, event);
    }

    @RequiredArgsConstructor
    @Builder
    static class TestEventFieldUpdater implements EventFieldUpdater {
        private final Function<EventRequest, Object> requestMapper;
        private final Function<Event, Object> eventMapper;
        private final Updater updater;

        @Override
        public Object getRequestField(EventRequest request) {
            return requestMapper.apply(request);
        }

        @Override
        public Object getEventField(Event event) {
            return eventMapper.apply(event);
        }

        @Override
        public void doUpdate(UpdateEventContext context, EventRequest request, Event event) {
            updater.update(context, request, event);
        }
    }

    interface Updater {
        void update(UpdateEventContext context, EventRequest request, Event event);
    }
}
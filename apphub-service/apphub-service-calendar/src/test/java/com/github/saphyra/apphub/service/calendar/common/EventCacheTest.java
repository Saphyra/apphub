package com.github.saphyra.apphub.service.calendar.common;

import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.EventDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class EventCacheTest {
    private static final UUID EVENT_ID = UUID.randomUUID();

    @Mock
    private EventDao eventDao;

    @InjectMocks
    private EventCache underTest;

    @Mock
    private Event event;

    @Test
    void get() {
        given(eventDao.findByIdValidated(EVENT_ID)).willReturn(event);

        assertThat(underTest.get(EVENT_ID)).isEqualTo(event);
        assertThat(underTest.get(EVENT_ID)).isEqualTo(event);

        then(eventDao).should(times(1)).findByIdValidated(EVENT_ID);
    }

    @Test
    void load() {
        given(eventDao.findAll()).willReturn(List.of(event));
        given(event.getEventId()).willReturn(EVENT_ID);

        underTest.load(AbstractDao::findAll);

        assertThat(underTest.get(EVENT_ID)).isEqualTo(event);
        then(eventDao).should().findAll();
        then(eventDao).shouldHaveNoMoreInteractions();
    }
}
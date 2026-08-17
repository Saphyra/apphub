package com.github.saphyra.apphub.service.feature.calendar.domain.share.service.type;

import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.service.object_query.EventObjectQueryService;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EventSharedObjectServiceTest {
    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final UUID OBJECT_ID = UUID.randomUUID();
    private static final UUID PARENT_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String TITLE = "Test Title";

    @Mock
    private EventObjectQueryService eventObjectQueryService;

    @Mock
    private EventDao eventDao;

    @InjectMocks
    private EventSharedObjectService underTest;

    @Mock
    private Event event;

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(SharedObjectType.EVENT);
    }

    @Test
    void getSharedObject() {
        given(eventObjectQueryService.findEvent(OWNER_ID, OBJECT_ID)).willReturn(java.util.Optional.of(event));
        given(event.getEventId()).willReturn(EVENT_ID);
        given(event.getUserId()).willReturn(USER_ID);
        given(event.getTitle()).willReturn(TITLE);

        SharedObject result = underTest.getSharedObject(OWNER_ID, OBJECT_ID, PARENT_ID);

        assertThat(result.objectId()).isEqualTo(EVENT_ID);
        assertThat(result.owner()).isEqualTo(USER_ID);
        assertThat(result.parent()).isEqualTo(USER_ID);
        assertThat(result.name()).isEqualTo(TITLE);
    }

    @Test
    void exists() {
        given(eventDao.findById(USER_ID, EVENT_ID)).willReturn(Optional.of(event));

        assertThat(underTest.exists(USER_ID, EVENT_ID)).isTrue();
    }
}
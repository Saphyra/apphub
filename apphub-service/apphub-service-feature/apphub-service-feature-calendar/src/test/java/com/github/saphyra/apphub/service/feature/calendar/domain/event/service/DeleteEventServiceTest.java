package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.service.feature.calendar.common.dao.CommonCalendarDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DeleteEventServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();

    @Mock
    private CommonCalendarDao commonCalendarDao;
    @InjectMocks
    private DeleteEventService underTest;

    @Test
    void delete() {
        underTest.delete(USER_ID, EVENT_ID);

        then(commonCalendarDao).should().deleteEvents(USER_ID, List.of(EVENT_ID));
    }
}
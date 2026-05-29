package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEvent;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEventDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ArchiveEventServiceTest {
    private static final UUID EVENT_ID = UUID.randomUUID();

    @Mock
    private DeprecatedEventDao eventDao;

    @InjectMocks
    private ArchiveEventService underTest;

    @Mock
    private DeprecatedEvent event;

    @Test
    void nullValue() {
        ExceptionValidator.validateInvalidParam(catchThrowable(() -> underTest.archive(EVENT_ID, null)), "archived", "must not be null");
    }

    @Test
    void archive() {
        given(eventDao.findByIdValidated(EVENT_ID)).willReturn(event);

        underTest.archive(EVENT_ID, true);

        then(event).should().setArchived(true);
        then(eventDao).should().save(event);
    }
}
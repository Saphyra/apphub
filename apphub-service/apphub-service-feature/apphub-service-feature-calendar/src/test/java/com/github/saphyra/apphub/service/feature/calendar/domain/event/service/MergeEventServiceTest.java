package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.api.feature.calendar.model.RepetitionType;
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

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class MergeEventServiceTest {
    private static final UUID PARENT_EVENT_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final UUID CHILD_EVENT_ID = UUID.randomUUID();
    private static final String CONTENT = "content";
    private static final String NOTE = "note";
    private static final LocalTime TIME = LocalTime.now();
    private static final Integer REMIND_ME_BEFORE_DAYS = 32;

    @Mock
    private DeprecatedEventDao eventDao;

    @Mock
    private DeprecatedOccurrenceDao occurrenceDao;

    @Mock
    private DeleteEventService deleteEventService;

    @InjectMocks
    private MergeEventService underTest;

    @Mock
    private DeprecatedEvent parent;

    @Mock
    private DeprecatedEvent child;

    @Mock
    private DeprecatedOccurrence occurrence;

    @Test
    void notOneTime() {
        given(eventDao.findByIdValidated(PARENT_EVENT_ID)).willReturn(parent);
        given(parent.getRepetitionType()).willReturn(RepetitionType.EVERY_X_DAYS);

        ExceptionValidator.validateInvalidParam(() -> underTest.merge(PARENT_EVENT_ID), "eventId", "invalid type");
    }

    @Test
    void sameEvent() {
        given(eventDao.findByIdValidated(PARENT_EVENT_ID)).willReturn(parent);
        given(parent.getRepetitionType()).willReturn(RepetitionType.ONE_TIME);
        given(parent.getUserId()).willReturn(USER_ID);
        given(eventDao.getByUserId(USER_ID)).willReturn(List.of(parent));
        given(parent.getEventId()).willReturn(PARENT_EVENT_ID);

        underTest.merge(PARENT_EVENT_ID);

        then(occurrenceDao).shouldHaveNoInteractions();
        then(deleteEventService).shouldHaveNoInteractions();
    }

    @Test
    void childNotOneTime() {
        given(eventDao.findByIdValidated(PARENT_EVENT_ID)).willReturn(parent);
        given(parent.getRepetitionType()).willReturn(RepetitionType.ONE_TIME);
        given(parent.getUserId()).willReturn(USER_ID);
        given(eventDao.getByUserId(USER_ID)).willReturn(List.of(child));
        given(child.getEventId()).willReturn(CHILD_EVENT_ID);
        given(child.getRepetitionType()).willReturn(RepetitionType.EVERY_X_DAYS);

        underTest.merge(PARENT_EVENT_ID);

        then(occurrenceDao).shouldHaveNoInteractions();
        then(deleteEventService).shouldHaveNoInteractions();
    }

    @Test
    void titleDoesNotMatch() {
        given(eventDao.findByIdValidated(PARENT_EVENT_ID)).willReturn(parent);
        given(parent.getRepetitionType()).willReturn(RepetitionType.ONE_TIME);
        given(parent.getUserId()).willReturn(USER_ID);
        given(eventDao.getByUserId(USER_ID)).willReturn(List.of(child));
        given(child.getEventId()).willReturn(CHILD_EVENT_ID);
        given(child.getRepetitionType()).willReturn(RepetitionType.ONE_TIME);
        given(child.getTitle()).willReturn("asd");
        given(parent.getTitle()).willReturn(TITLE);

        underTest.merge(PARENT_EVENT_ID);

        then(occurrenceDao).shouldHaveNoInteractions();
        then(deleteEventService).shouldHaveNoInteractions();
    }

    @Test
    void merge() {
        given(eventDao.findByIdValidated(PARENT_EVENT_ID)).willReturn(parent);
        given(parent.getEventId()).willReturn(PARENT_EVENT_ID);
        given(parent.getRepetitionType()).willReturn(RepetitionType.ONE_TIME);
        given(parent.getUserId()).willReturn(USER_ID);
        given(eventDao.getByUserId(USER_ID)).willReturn(List.of(child));
        given(child.getEventId()).willReturn(CHILD_EVENT_ID);
        given(child.getRepetitionType()).willReturn(RepetitionType.ONE_TIME);
        given(child.getTitle()).willReturn(TITLE);
        given(parent.getTitle()).willReturn(TITLE);
        given(occurrenceDao.getByEventId(CHILD_EVENT_ID)).willReturn(List.of(occurrence));
        given(child.getContent()).willReturn(CONTENT);
        given(occurrence.getNote()).willReturn(NOTE);
        given(occurrence.getTime()).willReturn(TIME);
        given(occurrence.getRemindMeBeforeDays()).willReturn(REMIND_ME_BEFORE_DAYS);
        given(child.getUserId()).willReturn(USER_ID);
        given(child.getRemindMeBeforeDays()).willReturn(null);

        underTest.merge(PARENT_EVENT_ID);

        then(occurrence).should().setEventId(PARENT_EVENT_ID);
        then(occurrence).should().setNote(String.join("\n\n", CONTENT, NOTE));
        then(occurrence).should().setTime(TIME);
        then(occurrence).should().setRemindMeBeforeDays(REMIND_ME_BEFORE_DAYS);
        then(occurrenceDao).should().save(occurrence);

        then(deleteEventService).should().delete(USER_ID, CHILD_EVENT_ID);
    }
}
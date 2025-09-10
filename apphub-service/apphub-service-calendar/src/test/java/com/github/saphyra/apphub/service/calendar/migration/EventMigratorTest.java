package com.github.saphyra.apphub.service.calendar.migration;

import com.github.saphyra.apphub.api.calendar.model.RepetitionType;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.EventDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EventMigratorTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final String REPETITION_DATA = "repetition-data";
    private static final Integer REPEAT = 123;
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final LocalDate CURRENT_DATE = LocalDate.now();
    private static final LocalDate START_DATE = CURRENT_DATE.minusDays(3);
    private static final LocalTime TIME = LocalTime.now();
    private static final long OFFSET_DAYS = 30L;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private MigrationProperties migrationProperties;

    @Mock
    private EventDao eventDao;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private OccurrenceMigrator occurrenceMigrator;

    @Mock
    private ErrorReporterService errorReporterService;

    @InjectMocks
    private EventMigrator underTest;

    @Mock
    private DeprecatedEvent deprecatedEvent;

    @Test
    void migrate() {
        given(deprecatedEvent.getEventId()).willReturn(EVENT_ID);
        given(deprecatedEvent.getUserId()).willReturn(USER_ID);
        given(deprecatedEvent.getRepetitionType()).willReturn(RepetitionType.EVERY_X_DAYS);
        given(deprecatedEvent.getRepetitionData()).willReturn(REPETITION_DATA);
        given(deprecatedEvent.getRepeat()).willReturn(REPEAT);
        given(deprecatedEvent.getStartDate()).willReturn(START_DATE);
        given(deprecatedEvent.getTime()).willReturn(TIME);
        given(deprecatedEvent.getTitle()).willReturn(TITLE);
        given(deprecatedEvent.getContent()).willReturn(CONTENT);

        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);
        given(migrationProperties.getDefaultEndDateOffset()).willReturn(Duration.ofDays(OFFSET_DAYS));

        underTest.migrate(deprecatedEvent);

        ArgumentCaptor<AccessTokenHeader> accessTokenHeaderArgumentCaptor = ArgumentCaptor.forClass(AccessTokenHeader.class);
        then(accessTokenProvider).should().set(accessTokenHeaderArgumentCaptor.capture());
        assertThat(accessTokenHeaderArgumentCaptor.getValue().getUserId()).isEqualTo(USER_ID);

        ArgumentCaptor<Event> eventArgumentCaptor = ArgumentCaptor.forClass(Event.class);
        then(eventDao).should().save(eventArgumentCaptor.capture());
        Event event = eventArgumentCaptor.getValue();
        then(occurrenceMigrator).should().migrate(event);

        assertThat(event)
            .returns(EVENT_ID, Event::getEventId)
            .returns(USER_ID, Event::getUserId)
            .returns(RepetitionType.EVERY_X_DAYS, Event::getRepetitionType)
            .returns(REPETITION_DATA, Event::getRepetitionData)
            .returns(REPEAT, Event::getRepeatForDays)
            .returns(START_DATE, Event::getStartDate)
            .returns(CURRENT_DATE.plusDays(OFFSET_DAYS), Event::getEndDate)
            .returns(TIME, Event::getTime)
            .returns(TITLE, Event::getTitle)
            .returns(CONTENT, Event::getContent)
            .returns(REPEAT, Event::getRepeatForDays);
    }

    @Test
    void migrateOneTimeEvent() {
        given(deprecatedEvent.getEventId()).willReturn(EVENT_ID);
        given(deprecatedEvent.getUserId()).willReturn(USER_ID);
        given(deprecatedEvent.getRepetitionType()).willReturn(RepetitionType.ONE_TIME);
        given(deprecatedEvent.getRepetitionData()).willReturn(REPETITION_DATA);
        given(deprecatedEvent.getRepeat()).willReturn(REPEAT);
        given(deprecatedEvent.getStartDate()).willReturn(START_DATE);
        given(deprecatedEvent.getTime()).willReturn(TIME);
        given(deprecatedEvent.getTitle()).willReturn(TITLE);
        given(deprecatedEvent.getContent()).willReturn(CONTENT);

        underTest.migrate(deprecatedEvent);

        ArgumentCaptor<AccessTokenHeader> accessTokenHeaderArgumentCaptor = ArgumentCaptor.forClass(AccessTokenHeader.class);
        then(accessTokenProvider).should().set(accessTokenHeaderArgumentCaptor.capture());
        assertThat(accessTokenHeaderArgumentCaptor.getValue().getUserId()).isEqualTo(USER_ID);

        ArgumentCaptor<Event> eventArgumentCaptor = ArgumentCaptor.forClass(Event.class);
        then(eventDao).should().save(eventArgumentCaptor.capture());
        Event event = eventArgumentCaptor.getValue();
        then(occurrenceMigrator).should().migrate(event);

        assertThat(event)
            .returns(EVENT_ID, Event::getEventId)
            .returns(USER_ID, Event::getUserId)
            .returns(RepetitionType.ONE_TIME, Event::getRepetitionType)
            .returns(REPETITION_DATA, Event::getRepetitionData)
            .returns(REPEAT, Event::getRepeatForDays)
            .returns(START_DATE, Event::getStartDate)
            .returns(START_DATE, Event::getEndDate)
            .returns(TIME, Event::getTime)
            .returns(TITLE, Event::getTitle)
            .returns(CONTENT, Event::getContent)
            .returns(REPEAT, Event::getRepeatForDays);
    }

    @Test
    void migrate_error(){
        RuntimeException exception = new RuntimeException("Test exception");

        given(deprecatedEvent.getUserId()).willThrow(exception);

        underTest.migrate(deprecatedEvent);

        then(errorReporterService).should().report(anyString(), eq(exception));
    }
}
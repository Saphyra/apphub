package com.github.saphyra.apphub.service.calendar.migration;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class CalendarMigrationControllerTest {
    @Mock
    private MigrationDao migrationDao;

    @Mock
    private EventMigrator eventMigrator;

    @Mock
    private ErrorReporterService errorReporterService;

    @InjectMocks
    private CalendarMigrationController underTest;

    @Mock
    private DeprecatedEvent deprecatedEvent;

    @Test
    void migrate() {
        given(migrationDao.getAllEvents()).willReturn(List.of(deprecatedEvent));

        underTest.migrate();

        then(eventMigrator).should().migrate(deprecatedEvent);
    }

    @Test
    void migrate_error() {
        given(migrationDao.getAllEvents()).willReturn(List.of(deprecatedEvent));

        Exception exception = new RuntimeException("test");
        doThrow(exception).when(eventMigrator).migrate(deprecatedEvent);

        underTest.migrate();

        then(errorReporterService).should().report(anyString(), eq(exception));
    }
}
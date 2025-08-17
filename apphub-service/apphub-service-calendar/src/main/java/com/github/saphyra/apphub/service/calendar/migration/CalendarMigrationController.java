package com.github.saphyra.apphub.service.calendar.migration;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class CalendarMigrationController {
    private final MigrationDao migrationDao;
    private final EventMigrator eventMigrator;
    private final ErrorReporterService errorReporterService;

    @PostMapping(MigrationConstants.URL)
    void migrate() {

        try {
            log.info("Calendar migration started.");

            List<DeprecatedEvent> events = migrationDao.getAllEvents();
            log.info("Found {} events to migrate.", events.size());
            events.forEach(eventMigrator::migrate);

            log.info("Calendar migration completed.");
        } catch (Exception e) {
            log.error("Migration failed.", e);
            errorReporterService.report("Migration failed.", e);
        }

    }
}

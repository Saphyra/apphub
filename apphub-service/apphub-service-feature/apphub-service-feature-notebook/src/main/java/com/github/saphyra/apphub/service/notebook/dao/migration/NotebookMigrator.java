package com.github.saphyra.apphub.service.notebook.dao.migration;

import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.migration.migrator.MigratorProvider;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class NotebookMigrator {
    private final ListItemViewQueryService listItemViewQueryService;
    private final AccessTokenProvider accessTokenProvider;
    private final DeprecatedListItemDao deprecatedListItemDao;
    private final ErrorReporterService errorReporterService;
    private final MigratorProvider migratorProvider;

    @PostConstruct
    void migrate() {
        log.info("Starting Notebook migration...");

        listItemViewQueryService.getAll()
            .forEach(bw -> migrate(bw.getEntity1(), bw.getEntity2()));

        log.info("Notebook migration finished.");
    }

    private void migrate(UUID userId, UUID listItemId) {
        log.info("Migrating ListItem {} for user {}", listItemId, userId);
        try (var _ = accessTokenProvider.set(AccessToken.builder().userId(userId).build())) {
            DeprecatedListItem listItem = deprecatedListItemDao.findByIdValidated(listItemId);
            log.info("ListItemType: {}", listItem.getType());

            migratorProvider.getForType(listItem.getType())
                .migrate(listItem);

            deprecatedListItemDao.delete(listItem);
            log.info("Successfully migrated ListItem {} for user {}", listItemId, userId);
        } catch (Exception e) {
            errorReporterService.report("Failed migrating ListItem " + listItemId + " for user " + userId, e);
        }
    }
}

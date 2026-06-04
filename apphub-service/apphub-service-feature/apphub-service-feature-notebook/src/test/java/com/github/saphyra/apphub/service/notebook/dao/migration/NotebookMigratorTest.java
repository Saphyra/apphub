package com.github.saphyra.apphub.service.notebook.dao.migration;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.migration.migrator.ListItemMigrator;
import com.github.saphyra.apphub.service.notebook.dao.migration.migrator.MigratorProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class NotebookMigratorTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private ListItemViewQueryService listItemViewQueryService;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private DeprecatedListItemDao deprecatedListItemDao;

    @Mock
    private ErrorReporterService errorReporterService;

    @Mock
    private MigratorProvider migratorProvider;

    @InjectMocks
    private NotebookMigrator underTest;

    @Mock
    private AutoCloseable tokenScope;

    @Mock
    private DeprecatedListItem listItem;

    @Mock
    private ListItemMigrator listItemMigrator;

    @Test
    void migrate() {
        given(listItemViewQueryService.getAll()).willReturn(List.of(new BiWrapper<>(USER_ID, LIST_ITEM_ID)));
        given(accessTokenProvider.set(any())).willReturn(tokenScope);
        given(deprecatedListItemDao.findByIdValidated(LIST_ITEM_ID)).willReturn(listItem);
        given(listItem.getType()).willReturn(ListItemType.TEXT);
        given(migratorProvider.getForType(ListItemType.TEXT)).willReturn(listItemMigrator);

        underTest.migrate();

        ArgumentCaptor<AccessToken> accessTokenCaptor = ArgumentCaptor.forClass(AccessToken.class);
        then(accessTokenProvider).should().set(accessTokenCaptor.capture());
        assertThat(accessTokenCaptor.getValue().getUserId()).isEqualTo(USER_ID);

        then(listItemMigrator).should().migrate(listItem);
        then(deprecatedListItemDao).should().delete(listItem);
        then(errorReporterService).shouldHaveNoInteractions();
    }

    @Test
    void migrate_failedMigration() {
        RuntimeException exception = new RuntimeException("test-exception");

        given(listItemViewQueryService.getAll()).willReturn(List.of(new BiWrapper<>(USER_ID, LIST_ITEM_ID)));
        given(accessTokenProvider.set(any())).willReturn(tokenScope);
        given(deprecatedListItemDao.findByIdValidated(LIST_ITEM_ID)).willThrow(exception);

        underTest.migrate();

        then(errorReporterService).should().report("Failed migrating ListItem " + LIST_ITEM_ID + " for user " + USER_ID, exception);
        then(deprecatedListItemDao).should(never()).delete(any());
    }

    @Test
    void migrate_continuesAfterFailure() {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        UUID listItemId1 = UUID.randomUUID();
        UUID listItemId2 = UUID.randomUUID();

        RuntimeException exception = new RuntimeException("test-exception");

        DeprecatedListItem listItem2 = DeprecatedListItem.builder()
            .listItemId(listItemId2)
            .userId(userId2)
            .type(ListItemType.TEXT)
            .title("title")
            .build();

        given(listItemViewQueryService.getAll()).willReturn(List.of(
            new BiWrapper<>(userId1, listItemId1),
            new BiWrapper<>(userId2, listItemId2)
        ));
        given(accessTokenProvider.set(any())).willReturn(tokenScope);
        given(deprecatedListItemDao.findByIdValidated(listItemId1)).willThrow(exception);
        given(deprecatedListItemDao.findByIdValidated(listItemId2)).willReturn(listItem2);
        given(migratorProvider.getForType(ListItemType.TEXT)).willReturn(listItemMigrator);

        underTest.migrate();

        then(errorReporterService).should().report("Failed migrating ListItem " + listItemId1 + " for user " + userId1, exception);
        then(listItemMigrator).should().migrate(listItem2);
        then(deprecatedListItemDao).should().delete(listItem2);

        ArgumentCaptor<AccessToken> accessTokenCaptor = ArgumentCaptor.forClass(AccessToken.class);
        then(accessTokenProvider).should(times(2)).set(accessTokenCaptor.capture());
        assertThat(accessTokenCaptor.getAllValues())
            .extracting(AccessToken::getUserId)
            .containsExactly(userId1, userId2);
    }
}



package com.github.saphyra.apphub.service.notebook.dao.migration.migrator;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.CommonListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ChecklistListItemMigratorTest {
    @Mock
    private ListItemMigratorUtil listItemMigratorUtil;

    @Mock
    private CommonListItemDao commonListItemDao;

    @InjectMocks
    private ChecklistListItemMigrator underTest;

    @Mock
    private DeprecatedListItem original;

    @Mock
    private ListItem listItem;

    @Mock
    private ChecklistItem checklistItem;

    @Mock
    private Content content;

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ListItemType.CHECKLIST);
    }

    @Test
    void migrate() {
        BiWrapper<ChecklistItem, Content> item = new BiWrapper<>(checklistItem, content);
        given(listItemMigratorUtil.migrate(original)).willReturn(listItem);
        given(listItemMigratorUtil.migrateChecklist(listItem)).willReturn(List.of(item));

        underTest.migrate(original);

        ArgumentCaptor<List<ChecklistItem>> checklistItemCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<Content>> contentCaptor = ArgumentCaptor.forClass(List.class);
        then(commonListItemDao).should().saveChecklist(org.mockito.ArgumentMatchers.eq(listItem), checklistItemCaptor.capture(), contentCaptor.capture());

        assertThat(checklistItemCaptor.getValue()).containsExactly(checklistItem);
        assertThat(contentCaptor.getValue()).containsExactly(content);
    }
}


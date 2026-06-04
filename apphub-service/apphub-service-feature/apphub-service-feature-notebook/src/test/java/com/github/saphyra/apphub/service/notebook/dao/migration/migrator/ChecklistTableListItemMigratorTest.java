package com.github.saphyra.apphub.service.notebook.dao.migration.migrator;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.lib.common_domain.TriWrapper;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.CommonListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ChecklistTableListItemMigratorTest {
    @Mock
    private ListItemMigratorUtil listItemMigratorUtil;

    @Mock
    private CommonListItemDao commonListItemDao;

    @Mock
    private DeprecatedListItem original;

    @Mock
    private ListItem listItem;

    @Mock
    private TableHead tableHead;

    @Mock
    private TableRow tableRow;

    @Mock
    private Content content;

    @Test
    void getType() {
        ChecklistTableListItemMigrator underTest = new ChecklistTableListItemMigrator(listItemMigratorUtil, commonListItemDao);

        assertThat(underTest.getType()).isEqualTo(ListItemType.CHECKLIST_TABLE);
    }

    @Test
    void migrate() {
        ChecklistTableListItemMigrator underTest = new ChecklistTableListItemMigrator(listItemMigratorUtil, commonListItemDao);
        List<TableHead> tableHeads = List.of(tableHead);
        List<TableRow> tableRows = List.of(tableRow);
        List<Content> contents = List.of(content);
        TriWrapper<List<TableHead>, List<TableRow>, List<Content>> table = new TriWrapper<>(tableHeads, tableRows, contents);

        given(listItemMigratorUtil.migrate(original)).willReturn(listItem);
        given(listItemMigratorUtil.migrateTable(listItem)).willReturn(table);

        underTest.migrate(original);

        then(commonListItemDao).should().saveTable(listItem, tableHeads, tableRows, contents);
    }
}


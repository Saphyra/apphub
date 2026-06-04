package com.github.saphyra.apphub.service.notebook.dao.migration.migrator;

import com.github.saphyra.apphub.lib.common_domain.TriWrapper;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.CommonListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRow;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
abstract class AbstractTableMigrator implements ListItemMigrator {
    private final ListItemMigratorUtil listItemMigratorUtil;
    private final CommonListItemDao commonListItemDao;

    @Override
    public void migrate(DeprecatedListItem original) {
        ListItem listItem = listItemMigratorUtil.migrate(original);
        TriWrapper<List<TableHead>, List<TableRow>, List<Content>> table = listItemMigratorUtil.migrateTable(listItem);

        commonListItemDao.saveTable(listItem, table.getEntity1(), table.getEntity2(), table.getEntity3());
    }
}

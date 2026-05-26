package com.github.saphyra.apphub.service.notebook.dao.migration.migrator;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.service.notebook.dao.list_item.CommonListItemDao;
import org.springframework.stereotype.Component;

@Component
class ChecklistTableListItemMigrator extends AbstractTableMigrator {
    ChecklistTableListItemMigrator(ListItemMigratorUtil listItemMigratorUtil, CommonListItemDao commonListItemDao) {
        super(listItemMigratorUtil, commonListItemDao);
    }

    @Override
    public ListItemType getType() {
        return ListItemType.CHECKLIST_TABLE;
    }
}

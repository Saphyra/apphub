package com.github.saphyra.apphub.service.notebook.dao.migration.migrator;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.CommonListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class ChecklistListItemMigrator implements ListItemMigrator {
    private final ListItemMigratorUtil listItemMigratorUtil;
    private final CommonListItemDao commonListItemDao;

    @Override
    public ListItemType getType() {
        return ListItemType.CHECKLIST;
    }

    @Override
    public void migrate(DeprecatedListItem original) {
        ListItem listItem = listItemMigratorUtil.migrate(original);
        List<BiWrapper<ChecklistItem, Content>> items = listItemMigratorUtil.migrateChecklist(listItem);

        commonListItemDao.saveChecklist(
            listItem,
            items.stream().map(BiWrapper::getEntity1).toList(),
            items.stream().map(BiWrapper::getEntity2).toList()
        );
    }
}

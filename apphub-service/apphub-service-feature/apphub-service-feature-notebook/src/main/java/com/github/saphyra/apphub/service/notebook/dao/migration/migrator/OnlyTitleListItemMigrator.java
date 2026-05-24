package com.github.saphyra.apphub.service.notebook.dao.migration.migrator;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
//TODO unit test
class OnlyTitleListItemMigrator implements ListItemMigrator {
    private final ListItemDao listItemDao;
    private final ListItemMigratorUtil  listItemMigratorUtil;

    @Override
    public ListItemType getType() {
        return ListItemType.ONLY_TITLE;
    }

    @Override
    public void migrate(DeprecatedListItem original) {
        ListItem listItem = listItemMigratorUtil.migrate(original);

        listItemDao.save(listItem);
    }
}

package com.github.saphyra.apphub.service.notebook.dao.migration.migrator;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_content.DeprecatedContent;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_content.DeprecatedContentDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class LinkMigrator implements ListItemMigrator {
    private final DeprecatedContentDao contentDao;
    private final ListItemMigratorUtil listItemMigratorUtil;
    private final ListItemDao listItemDao;

    @Override
    public ListItemType getType() {
        return ListItemType.LINK;
    }

    @Override
    public void migrate(DeprecatedListItem original) {
        DeprecatedContent content = contentDao.findByParentValidated(original.getListItemId());
        ListItem listItem = listItemMigratorUtil.migrate(original, content.getContent());

        listItemDao.save(listItem);
    }
}

package com.github.saphyra.apphub.service.notebook.dao.migration.migrator;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_file.File;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_file.FileDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class FileListItemMigrator implements ListItemMigrator {
    private final ListItemMigratorUtil listItemMigratorUtil;
    private final FileDao fileDao;
    private final UuidConverter uuidConverter;
    private final ListItemDao listItemDao;

    @Override
    public ListItemType getType() {
        return ListItemType.FILE;
    }

    @Override
    public void migrate(DeprecatedListItem original) {
        File file = fileDao.findByParentValidated(original.getListItemId());
        ListItem listItem = listItemMigratorUtil.migrate(original, uuidConverter.convertDomain(file.getStoredFileId()));

        listItemDao.save(listItem);
    }
}

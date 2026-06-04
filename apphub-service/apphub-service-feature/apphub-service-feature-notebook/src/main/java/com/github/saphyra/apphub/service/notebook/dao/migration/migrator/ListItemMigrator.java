package com.github.saphyra.apphub.service.notebook.dao.migration.migrator;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;

public interface ListItemMigrator {
    ListItemType getType();

    void migrate(DeprecatedListItem original);
}

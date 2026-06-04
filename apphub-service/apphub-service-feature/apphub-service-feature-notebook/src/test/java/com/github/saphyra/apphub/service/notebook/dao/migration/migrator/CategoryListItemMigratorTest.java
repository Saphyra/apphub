package com.github.saphyra.apphub.service.notebook.dao.migration.migrator;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CategoryListItemMigratorTest {
    @Mock
    private ListItemDao listItemDao;

    @Mock
    private ListItemMigratorUtil listItemMigratorUtil;

    @InjectMocks
    private CategoryListItemMigrator underTest;

    @Mock
    private DeprecatedListItem original;

    @Mock
    private ListItem listItem;

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ListItemType.CATEGORY);
    }

    @Test
    void migrate() {
        given(listItemMigratorUtil.migrate(original)).willReturn(listItem);

        underTest.migrate(original);

        then(listItemDao).should().save(listItem);
    }
}


package com.github.saphyra.apphub.service.notebook.dao.migration.migrator;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_content.DeprecatedContent;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_content.DeprecatedContentDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class LinkMigratorTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final String LINK_URL = "https://example.com";

    @Mock
    private DeprecatedContentDao contentDao;

    @Mock
    private ListItemMigratorUtil listItemMigratorUtil;

    @Mock
    private ListItemDao listItemDao;

    @InjectMocks
    private LinkMigrator underTest;

    @Mock
    private DeprecatedListItem original;

    @Mock
    private ListItem listItem;

    @Mock
    private DeprecatedContent content;

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ListItemType.LINK);
    }

    @Test
    void migrate() {
        given(content.getContent()).willReturn(LINK_URL);
        given(original.getListItemId()).willReturn(LIST_ITEM_ID);
        given(contentDao.findByParentValidated(LIST_ITEM_ID)).willReturn(content);
        given(listItemMigratorUtil.migrate(original, LINK_URL)).willReturn(listItem);

        underTest.migrate(original);

        then(listItemDao).should().save(listItem);
    }
}


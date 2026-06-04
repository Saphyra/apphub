package com.github.saphyra.apphub.service.notebook.dao.migration.migrator;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_file.File;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_file.FileDao;
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
class FileListItemMigratorTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID STORED_FILE_ID = UUID.randomUUID();
    private static final String STORED_FILE_ID_STRING = "stored-file-id";

    @Mock
    private ListItemMigratorUtil listItemMigratorUtil;

    @Mock
    private FileDao fileDao;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private ListItemDao listItemDao;

    @InjectMocks
    private FileListItemMigrator underTest;

    @Mock
    private DeprecatedListItem original;

    @Mock
    private ListItem listItem;

    @Mock
    private File file;

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ListItemType.FILE);
    }

    @Test
    void migrate() {
        given(file.getStoredFileId()).willReturn(STORED_FILE_ID);
        given(original.getListItemId()).willReturn(LIST_ITEM_ID);
        given(fileDao.findByParentValidated(LIST_ITEM_ID)).willReturn(file);
        given(uuidConverter.convertDomain(STORED_FILE_ID)).willReturn(STORED_FILE_ID_STRING);
        given(listItemMigratorUtil.migrate(original, STORED_FILE_ID_STRING)).willReturn(listItem);

        underTest.migrate(original);

        then(listItemDao).should().save(listItem);
    }
}


package com.github.saphyra.apphub.service.notebook.service.table.column_data.base.file;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class FileBasedColumnProxyTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID ROW_ID = UUID.randomUUID();

    @Mock
    private FileBasedColumnSaver fileBasedColumnSaver;

    @Mock
    private FileBasedColumnDeleter fileBasedColumnDeleter;

    @Mock
    private FileBasedColumnEditer fileBasedColumnEditer;

    @Mock
    private FileBasedColumnCloner fileBasedColumnCloner;

    @InjectMocks
    private FileBasedColumnProxy underTest;

    @Mock
    private TableFileUploadResponse fileUpload;

    @Mock
    private TableColumnModel model;

    @Mock
    private Dimension column;

    @Mock
    private ListItem listItem;

    @Test
    void save() {
        given(fileBasedColumnSaver.save(USER_ID, ROW_ID, model, ColumnType.FILE)).willReturn(Optional.of(fileUpload));

        assertThat(underTest.save(USER_ID, ROW_ID, model, ColumnType.FILE)).contains(fileUpload);
    }

    @Test
    void delete() {
        underTest.delete(column);

        then(fileBasedColumnDeleter).should().delete(column);
    }

    @Test
    void edit() {
        given(fileBasedColumnEditer.edit(listItem, ROW_ID, model)).willReturn(Optional.of(fileUpload));

        assertThat(underTest.edit(listItem, ROW_ID, model)).contains(fileUpload);
    }

    @Test
    void cloneColumn() {
        underTest.clone(listItem, ROW_ID, column, ColumnType.FILE);

        then(fileBasedColumnCloner).should().clone(listItem, ROW_ID, column, ColumnType.FILE);
    }
}
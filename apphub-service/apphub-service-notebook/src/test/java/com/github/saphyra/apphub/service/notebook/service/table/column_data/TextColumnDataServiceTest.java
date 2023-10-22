package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDto;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeFactory;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.service.ContentFactory;
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
class TextColumnDataServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = USER_ID;
    private static final UUID ROW_ID = USER_ID;
    private static final Integer COLUMN_INDEX = 234;
    private static final UUID COLUMN_ID = UUID.randomUUID();
    private static final String DATA = "data";
    private static final UUID CLONED_COLUMN_ID = UUID.randomUUID();

    @Mock
    private DimensionFactory dimensionFactory;

    @Mock
    private DimensionDao dimensionDao;

    @Mock
    private ContentFactory contentFactory;

    @Mock
    private ContentDao contentDao;

    @Mock
    private ColumnTypeFactory columnTypeFactory;

    @Mock
    private ColumnTypeDao columnTypeDao;

    @InjectMocks
    private TextColumnDataService underTest;

    @Mock
    private TableColumnModel model;

    @Mock
    private Dimension column;

    @Mock
    private Content content;

    @Mock
    private ColumnTypeDto columnType;

    @Mock
    private ListItem listItem;

    @Mock
    private Dimension clonedColumn;

    @Mock
    private Content clonedContent;

    @Mock
    private ColumnTypeDto clonedColumnType;

    @Test
    void canProcess() {
        assertThat(underTest.canProcess(ColumnType.TEXT)).isTrue();
    }

    @Test
    void save() {
        given(model.getColumnIndex()).willReturn(COLUMN_INDEX);
        given(model.getData()).willReturn(DATA);
        given(dimensionFactory.create(USER_ID, ROW_ID, COLUMN_INDEX)).willReturn(column);
        given(column.getDimensionId()).willReturn(COLUMN_ID);
        given(contentFactory.create(LIST_ITEM_ID, COLUMN_ID, USER_ID, DATA)).willReturn(content);
        given(columnTypeFactory.create(COLUMN_ID, USER_ID, ColumnType.TEXT)).willReturn(columnType);

        assertThat(underTest.save(USER_ID, LIST_ITEM_ID, ROW_ID, model)).isEmpty();

        then(dimensionDao).should().save(column);
        then(contentDao).should().save(content);
        then(columnTypeDao).should().save(columnType);
    }

    @Test
    void getData() {
        given(contentDao.findByParentValidated(COLUMN_ID)).willReturn(content);
        given(content.getContent()).willReturn(DATA);

        assertThat(underTest.getData(COLUMN_ID)).isEqualTo(DATA);
    }

    @Test
    void delete() {
        given(column.getDimensionId()).willReturn(COLUMN_ID);

        underTest.delete(column);

        then(contentDao).should().deleteByParent(COLUMN_ID);
        then(columnTypeDao).should().deleteById(COLUMN_ID);
        then(dimensionDao).should().delete(column);
    }

    @Test
    void edit() {
        given(model.getColumnId()).willReturn(COLUMN_ID);
        given(model.getColumnIndex()).willReturn(COLUMN_INDEX);
        given(model.getData()).willReturn(DATA);
        given(dimensionDao.findByIdValidated(COLUMN_ID)).willReturn(column);
        given(column.getDimensionId()).willReturn(COLUMN_ID);
        given(contentDao.findByParentValidated(COLUMN_ID)).willReturn(content);

        assertThat(underTest.edit(listItem, ROW_ID, model)).isEmpty();

        then(column).should().setIndex(COLUMN_INDEX);
        then(dimensionDao).should().save(column);
        then(content).should().setContent(DATA);
        then(contentDao).should().save(content);
    }

    @Test
    void cloneItem() {
        given(listItem.getUserId()).willReturn(USER_ID);
        given(column.getIndex()).willReturn(COLUMN_INDEX);
        given(dimensionFactory.create(USER_ID, ROW_ID, COLUMN_INDEX)).willReturn(clonedColumn);
        given(clonedColumn.getDimensionId()).willReturn(CLONED_COLUMN_ID);
        given(columnTypeFactory.create(CLONED_COLUMN_ID, USER_ID, ColumnType.TEXT)).willReturn(clonedColumnType);
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);
        given(column.getDimensionId()).willReturn(COLUMN_ID);
        given(contentDao.findByParentValidated(COLUMN_ID)).willReturn(content);
        given(content.getContent()).willReturn(DATA);
        given(contentFactory.create(LIST_ITEM_ID, CLONED_COLUMN_ID, USER_ID, DATA)).willReturn(clonedContent);

        underTest.clone(listItem, ROW_ID, column);

        then(dimensionDao).should().save(clonedColumn);
        then(columnTypeDao).should().save(clonedColumnType);
        then(contentDao).should().save(clonedContent);
    }
}
package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.base.content.ContentBasedColumnDataService;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.base.content.ContentBasedColumnTypeProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ContentBasedColumnDataServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID ROW_ID = UUID.randomUUID();
    private static final Object DATA = "data";
    private static final String CONTENT = "content";
    private static final Integer COLUMN_INDEX = 23;
    private static final UUID COLUMN_ID = UUID.randomUUID();

    @Mock
    private ContentDao contentDao;

    @Mock
    private ContentBasedColumnTypeProxy proxy;

    private ContentBasedColumnDataService underTest;

    @Mock
    private TableColumnModel model;

    @Mock
    private Content content;

    @Mock
    private Dimension column;

    @Mock
    private ListItem listItem;

    @BeforeEach
    void setUp() {
        underTest = new ContentBasedColumnDataServiceImp(ColumnType.TEXT, contentDao, proxy);
    }

    @Test
    void canProcess() {
        assertThat(underTest.canProcess(ColumnType.TEXT)).isTrue();
        assertThat(underTest.canProcess(ColumnType.LINK)).isFalse();
    }

    @Test
    void save() {
        given(model.getData()).willReturn(DATA);
        given(model.getColumnIndex()).willReturn(COLUMN_INDEX);

        assertThat(underTest.save(USER_ID, LIST_ITEM_ID, ROW_ID, model)).isEmpty();

        then(proxy).should().save(USER_ID, LIST_ITEM_ID, ROW_ID, COLUMN_INDEX, CONTENT, ColumnType.TEXT);
    }

    @Test
    void getData() {
        given(contentDao.findByParentValidated(COLUMN_ID)).willReturn(content);
        given(content.getContent()).willReturn(CONTENT);

        assertThat(underTest.getData(COLUMN_ID)).isEqualTo(CONTENT);
    }

    @Test
    void delete() {
        underTest.delete(column);

        then(proxy).should().delete(column);
    }

    @Test
    void edit() {
        given(model.getColumnId()).willReturn(COLUMN_ID);
        given(model.getColumnIndex()).willReturn(COLUMN_INDEX);
        given(model.getData()).willReturn(DATA);

        assertThat(underTest.edit(listItem, ROW_ID, model)).isEmpty();

        then(proxy).should().edit(COLUMN_ID, COLUMN_INDEX, CONTENT);
    }

    @Test
    void cloneColumn() {
        given(listItem.getUserId()).willReturn(USER_ID);
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);
        given(column.getDimensionId()).willReturn(COLUMN_ID);
        given(column.getIndex()).willReturn(COLUMN_INDEX);

        underTest.clone(listItem, ROW_ID, column);

        then(proxy).should().clone(USER_ID, LIST_ITEM_ID, ROW_ID, COLUMN_ID, COLUMN_INDEX, ColumnType.TEXT);
    }

    private static class ContentBasedColumnDataServiceImp extends ContentBasedColumnDataService {

        private ContentBasedColumnDataServiceImp(ColumnType columnType, ContentDao contentDao, ContentBasedColumnTypeProxy proxy) {
            super(columnType, contentDao, proxy);
        }

        @Override
        protected String stringifyContent(Object data) {
            assertThat(data).isEqualTo(DATA);
            return CONTENT;
        }

        @Override
        public void validateData(Object data) {

        }
    }
}
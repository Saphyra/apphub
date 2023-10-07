package com.github.saphyra.apphub.service.notebook.service.table.creation;

import com.github.saphyra.apphub.api.notebook.model.request.CreateTableRequest;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoin;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoinDao;
import com.github.saphyra.apphub.service.notebook.service.ListItemFactory;
import com.github.saphyra.apphub.service.notebook.service.table.TableHeadFactory;
import com.github.saphyra.apphub.service.notebook.service.table.TableJoinFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TableCreationServiceTest {
    private static final String TITLE = "title";
    private static final UUID PARENT = UUID.randomUUID();
    private static final String COLUMN_NAME = "column-name";
    private static final String COLUMN_VALUE = "column-value";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private ListItemFactory listItemFactory;

    @Mock
    private TableCreationRequestValidator tableCreationRequestValidator;

    @Mock
    private TableHeadFactory tableHeadFactory;

    @Mock
    private TableJoinFactory tableJoinFactory;

    @Mock
    private ContentDao contentDao;

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private TableHeadDao tableHeadDao;

    @Mock
    private TableJoinDao tableJoinDao;

    @InjectMocks
    private TableCreationService underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private TableHead tableHead;

    @Mock
    private TableJoin tableJoin;

    @Mock
    private Content content1;

    @Mock
    private Content content2;

    @Test
    public void create() {
        given(listItemFactory.create(USER_ID, TITLE, PARENT, ListItemType.TABLE)).willReturn(listItem);
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);
        given(tableHeadFactory.create(LIST_ITEM_ID, Arrays.asList(COLUMN_NAME), USER_ID)).willReturn(Arrays.asList(new BiWrapper<>(tableHead, content1)));
        given(tableJoinFactory.create(LIST_ITEM_ID, Arrays.asList(Arrays.asList(COLUMN_VALUE)), USER_ID)).willReturn(Arrays.asList(new BiWrapper<>(tableJoin, content2)));

        CreateTableRequest request = CreateTableRequest.builder()
            .title(TITLE)
            .parent(PARENT)
            .columnNames(Arrays.asList(COLUMN_NAME))
            .columns(Arrays.asList(Arrays.asList(COLUMN_VALUE)))
            .build();

        UUID result = underTest.create(request, USER_ID, ListItemType.TABLE);

        verify(tableCreationRequestValidator).validate(request);
        verify(listItemDao).save(listItem);
        verify(tableHeadDao).save(tableHead);
        verify(tableJoinDao).save(tableJoin);
        verify(contentDao).save(content1);
        verify(contentDao).save(content2);

        assertThat(result).isEqualTo(LIST_ITEM_ID);
    }
}
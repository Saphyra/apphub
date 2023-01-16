package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.api.notebook.model.request.EditListItemRequest;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import com.github.saphyra.apphub.service.notebook.service.text.ContentValidator;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ListItemEditionServiceTest {
    private static final String NEW_TITLE = "new-title";
    private static final UUID NEW_PARENT = UUID.randomUUID();
    private static final String NEW_VALUE = "new-value";
    private static final UUID LIST_ITEM_ID_1 = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID_2 = UUID.randomUUID();

    @Mock
    private ContentDao contentDao;

    @Mock
    private ContentValidator contentValidator;

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private ListItemRequestValidator listItemRequestValidator;

    @InjectMocks
    private ListItemEditionService underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private Content content;

    @Mock
    private ListItem child1;

    @Mock
    private ListItem child2;

    @BeforeEach
    public void setUp() {
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID_1);
    }

    @Test
    public void editLink() {
        given(listItemDao.findByIdValidated(LIST_ITEM_ID_1)).willReturn(listItem);
        given(listItem.getType()).willReturn(ListItemType.LINK);

        given(contentDao.findByParentValidated(LIST_ITEM_ID_1)).willReturn(content);

        EditListItemRequest request = EditListItemRequest.builder()
            .title(NEW_TITLE)
            .parent(NEW_PARENT)
            .value(NEW_VALUE)
            .build();

        underTest.edit(LIST_ITEM_ID_1, request);

        verify(listItemRequestValidator).validate(NEW_TITLE, NEW_PARENT);
        verify(contentValidator).validate(NEW_VALUE, "value");
        verify(listItem).setTitle(NEW_TITLE);
        verify(listItem).setParent(NEW_PARENT);
        verify(content).setContent(NEW_VALUE);
        verify(contentDao).save(content);
        verify(listItemDao, times(2)).save(listItem);
    }

    @Test
    public void editCategory_ownChild() {
        given(listItemDao.findByIdValidated(LIST_ITEM_ID_1)).willReturn(listItem);
        given(listItem.getType()).willReturn(ListItemType.CATEGORY);
        given(listItem.getUserId()).willReturn(USER_ID);

        given(listItemDao.getByUserIdAndParent(USER_ID, LIST_ITEM_ID_1)).willReturn(Arrays.asList(child1));
        given(child1.getListItemId()).willReturn(LIST_ITEM_ID_2);

        given(listItemDao.getByUserIdAndParent(USER_ID, LIST_ITEM_ID_2)).willReturn(Arrays.asList(child2));
        given(child2.getListItemId()).willReturn(NEW_PARENT);

        EditListItemRequest request = EditListItemRequest.builder()
            .title(NEW_TITLE)
            .parent(NEW_PARENT)
            .value(NEW_VALUE)
            .build();

        Throwable ex = catchThrowable(() -> underTest.edit(LIST_ITEM_ID_1, request));

        ExceptionValidator.validateInvalidParam(ex, "parent", "must not be own child");
    }

    @Test
    public void editCategory() {
        given(listItemDao.findByIdValidated(LIST_ITEM_ID_1)).willReturn(listItem);
        given(listItem.getType()).willReturn(ListItemType.CATEGORY);
        given(listItem.getUserId()).willReturn(USER_ID);

        given(listItemDao.getByUserIdAndParent(USER_ID, LIST_ITEM_ID_1)).willReturn(Collections.emptyList());

        EditListItemRequest request = EditListItemRequest.builder()
            .title(NEW_TITLE)
            .parent(NEW_PARENT)
            .value(NEW_VALUE)
            .build();

        underTest.edit(LIST_ITEM_ID_1, request);

        verify(listItemRequestValidator).validate(NEW_TITLE, NEW_PARENT);
        verify(listItem).setTitle(NEW_TITLE);
        verify(listItem).setParent(NEW_PARENT);
        verify(listItemDao, times(2)).save(listItem);
    }

    @Test
    public void editListItem() {
        given(listItemDao.findByIdValidated(LIST_ITEM_ID_1)).willReturn(listItem);
        given(listItem.getType()).willReturn(ListItemType.TEXT);

        EditListItemRequest request = EditListItemRequest.builder()
            .title(NEW_TITLE)
            .parent(NEW_PARENT)
            .value(NEW_VALUE)
            .build();

        underTest.edit(LIST_ITEM_ID_1, request);

        verify(listItemRequestValidator).validate(NEW_TITLE, NEW_PARENT);
        verify(listItem).setTitle(NEW_TITLE);
        verify(listItem).setParent(NEW_PARENT);
        verify(listItemDao, times(2)).save(listItem);
    }
}
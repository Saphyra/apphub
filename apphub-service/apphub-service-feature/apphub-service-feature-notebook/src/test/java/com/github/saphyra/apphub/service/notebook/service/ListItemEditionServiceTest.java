package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.request.EditListItemRequest;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.service.validator.ListItemRequestValidator;
import com.github.saphyra.apphub.service.notebook.service.validator.TextValidator;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ListItemEditionServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID PARENT = UUID.randomUUID();
    private static final UUID CHILD_ID = UUID.randomUUID();
    private static final UUID GRANDCHILD_ID = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final String VALUE = "value";

    @Mock
    private TextValidator textValidator;

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private ListItemRequestValidator listItemRequestValidator;

    @InjectMocks
    private ListItemEditionService underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private ListItem child;

    @Mock
    private ListItem grandchild;

    @Test
    void edit_link() {
        EditListItemRequest request = EditListItemRequest.builder()
            .parent(PARENT)
            .title(TITLE)
            .value(VALUE)
            .build();

        given(listItemDao.findByIdValidated(USER_ID, LIST_ITEM_ID)).willReturn(listItem);
        given(listItem.getType()).willReturn(ListItemType.LINK);
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);
        given(listItem.getUserId()).willReturn(USER_ID);
        given(listItemDao.getByUserIdAndParent(USER_ID, LIST_ITEM_ID)).willReturn(List.of());

        underTest.edit(USER_ID, LIST_ITEM_ID, request);

        then(listItemRequestValidator).should().validate(USER_ID, TITLE, PARENT);
        then(textValidator).should().validate(VALUE, "value");
        then(listItem).should().setData(VALUE);
        then(listItem).should().setTitle(TITLE);
        then(listItem).should().setParent(PARENT);
        then(listItemDao).should(times(2)).save(listItem);
    }

    @Test
    void edit_nonLink() {
        EditListItemRequest request = EditListItemRequest.builder()
            .parent(PARENT)
            .title(TITLE)
            .value(VALUE)
            .build();

        given(listItemDao.findByIdValidated(USER_ID, LIST_ITEM_ID)).willReturn(listItem);
        given(listItem.getType()).willReturn(ListItemType.TEXT);
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);
        given(listItem.getUserId()).willReturn(USER_ID);
        given(listItemDao.getByUserIdAndParent(USER_ID, LIST_ITEM_ID)).willReturn(List.of());

        underTest.edit(USER_ID, LIST_ITEM_ID, request);

        then(textValidator).should(never()).validate(VALUE, "value");
        then(listItem).should(never()).setData(VALUE);
        then(listItem).should().setTitle(TITLE);
    }

    @Test
    void moveListItem_parentIsOwnId() {
        given(listItemDao.findByIdValidated(USER_ID, LIST_ITEM_ID)).willReturn(listItem);
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);
        given(listItem.getUserId()).willReturn(USER_ID);
        given(listItemDao.getByUserIdAndParent(USER_ID, LIST_ITEM_ID)).willReturn(List.of());

        Throwable ex = catchThrowable(() -> underTest.moveListItem(USER_ID, LIST_ITEM_ID, LIST_ITEM_ID));

        ExceptionValidator.validateInvalidParam(ex, "parent", "must not be own child");
        then(listItemDao).should(never()).save(listItem);
    }

    @Test
    void moveListItem_parentIsChild() {
        given(listItemDao.findByIdValidated(USER_ID, LIST_ITEM_ID)).willReturn(listItem);
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);
        given(listItem.getUserId()).willReturn(USER_ID);
        given(listItemDao.getByUserIdAndParent(USER_ID, LIST_ITEM_ID)).willReturn(List.of(child));
        given(child.getListItemId()).willReturn(CHILD_ID);

        Throwable ex = catchThrowable(() -> underTest.moveListItem(USER_ID, LIST_ITEM_ID, CHILD_ID));

        ExceptionValidator.validateInvalidParam(ex, "parent", "must not be own child");
        then(listItemDao).should(never()).save(listItem);
    }

    @Test
    void moveListItem_parentIsDescendant() {
        given(listItemDao.findByIdValidated(USER_ID, LIST_ITEM_ID)).willReturn(listItem);
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);
        given(listItem.getUserId()).willReturn(USER_ID);
        given(listItemDao.getByUserIdAndParent(USER_ID, LIST_ITEM_ID)).willReturn(List.of(child));
        given(child.getListItemId()).willReturn(CHILD_ID);
        given(listItemDao.getByUserIdAndParent(USER_ID, CHILD_ID)).willReturn(List.of(grandchild));
        given(grandchild.getListItemId()).willReturn(GRANDCHILD_ID);

        Throwable ex = catchThrowable(() -> underTest.moveListItem(USER_ID, LIST_ITEM_ID, GRANDCHILD_ID));

        ExceptionValidator.validateInvalidParam(ex, "parent", "must not be own child");
        then(listItemDao).should(never()).save(listItem);
    }

    @Test
    void moveListItem() {
        given(listItemDao.findByIdValidated(USER_ID, LIST_ITEM_ID)).willReturn(listItem);
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);
        given(listItem.getUserId()).willReturn(USER_ID);
        given(listItemDao.getByUserIdAndParent(USER_ID, LIST_ITEM_ID)).willReturn(List.of());

        underTest.moveListItem(USER_ID, LIST_ITEM_ID, PARENT);

        then(listItem).should().setParent(PARENT);
        then(listItemDao).should().save(listItem);
    }

    @Test
    void moveListItem_parentIsOldParent() {
        given(listItemDao.findByIdValidated(USER_ID, LIST_ITEM_ID)).willReturn(listItem);
        given(listItem.getParent()).willReturn(PARENT);

        underTest.moveListItem(USER_ID, LIST_ITEM_ID, PARENT);

        then(listItemDao).should(never()).save(any());
    }
}
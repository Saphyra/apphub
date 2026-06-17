package com.github.saphyra.apphub.service.notebook.service.pin;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.pin_group.PinGroup;
import com.github.saphyra.apphub.service.notebook.dao.pin_group.PinGroupDao;
import com.github.saphyra.apphub.service.notebook.service.NotebookViewFactory;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PinServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID PIN_GROUP_ID = UUID.randomUUID();

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private NotebookViewFactory notebookViewFactory;

    @Mock
    private PinGroupDao pinGroupDao;

    @InjectMocks
    private PinService underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private NotebookView notebookView;

    @Mock
    private PinGroup pinGroup;

    @Test
    void pinListItem_nullPinned() {
        Throwable ex = catchThrowable(() -> underTest.pinListItem(USER_ID, LIST_ITEM_ID, null));

        ExceptionValidator.validateInvalidParam(ex, "pinned", "must not be null");
    }

    @Test
    void pinListItem() {
        given(listItemDao.findByIdValidated(USER_ID, LIST_ITEM_ID)).willReturn(listItem);

        underTest.pinListItem(USER_ID, LIST_ITEM_ID, true);

        then(listItem).should().setPinned(true);
        then(listItemDao).should().save(listItem);
    }

    @Test
    void getPinnedItems_nullPinGroupId() {
        ListItem pinnedItem = ListItem.builder()
            .listItemId(LIST_ITEM_ID)
            .userId(USER_ID)
            .title("title")
            .type(ListItemType.CATEGORY)
            .pinned(true)
            .build();

        given(listItemDao.getByUserId(USER_ID)).willReturn(List.of(pinnedItem));
        given(notebookViewFactory.create(pinnedItem)).willReturn(notebookView);

        List<NotebookView> result = underTest.getPinnedItems(USER_ID, null);

        assertThat(result).containsExactly(notebookView);
        then(pinGroupDao).shouldHaveNoInteractions();
    }

    @Test
    void getPinnedItems_notPinned() {
        ListItem notPinnedItem = ListItem.builder()
            .listItemId(LIST_ITEM_ID)
            .userId(USER_ID)
            .title("title")
            .type(ListItemType.CATEGORY)
            .pinned(false)
            .build();

        given(listItemDao.getByUserId(USER_ID)).willReturn(List.of(notPinnedItem));

        List<NotebookView> result = underTest.getPinnedItems(USER_ID, null);

        assertThat(result).isEmpty();
    }

    @Test
    void getPinnedItems_withPinGroupId() {
        UUID otherListItemId = UUID.randomUUID();

        ListItem pinnedInGroup = ListItem.builder()
            .listItemId(LIST_ITEM_ID)
            .userId(USER_ID)
            .title("title")
            .type(ListItemType.CATEGORY)
            .pinned(true)
            .build();

        ListItem pinnedNotInGroup = ListItem.builder()
            .listItemId(otherListItemId)
            .userId(USER_ID)
            .title("other")
            .type(ListItemType.CATEGORY)
            .pinned(true)
            .build();

        given(pinGroupDao.findByIdValidated(USER_ID, PIN_GROUP_ID)).willReturn(pinGroup);
        given(pinGroup.getListItemIds()).willReturn(Set.of(LIST_ITEM_ID));
        given(listItemDao.getByUserId(USER_ID)).willReturn(List.of(pinnedInGroup, pinnedNotInGroup));
        given(notebookViewFactory.create(pinnedInGroup)).willReturn(notebookView);

        List<NotebookView> result = underTest.getPinnedItems(USER_ID, PIN_GROUP_ID);

        assertThat(result).containsExactly(notebookView);
    }
}
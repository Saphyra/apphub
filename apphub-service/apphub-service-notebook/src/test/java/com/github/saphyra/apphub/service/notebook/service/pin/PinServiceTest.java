package com.github.saphyra.apphub.service.notebook.service.pin;

import com.github.saphyra.apphub.api.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.pin.mapping.PinMapping;
import com.github.saphyra.apphub.service.notebook.dao.pin.mapping.PinMappingDao;
import com.github.saphyra.apphub.service.notebook.service.NotebookViewFactory;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PinServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PIN_GROUP_ID = UUID.randomUUID();

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private PinMappingDao pinMappingDao;

    @Mock
    private NotebookViewFactory notebookViewFactory;

    @InjectMocks
    private PinService underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private NotebookView notebookView;

    @Mock
    private PinMapping pinMapping;

    @Test
    public void nullPinned() {
        Throwable ex = catchThrowable(() -> underTest.pinListItem(LIST_ITEM_ID, null));

        ExceptionValidator.validateInvalidParam(ex, "pinned", "must not be null");
    }

    @Test
    public void pinListItem() {
        given(listItemDao.findByIdValidated(LIST_ITEM_ID)).willReturn(listItem);

        underTest.pinListItem(LIST_ITEM_ID, true);

        verify(listItem).setPinned(true);
        verify(listItemDao).save(listItem);
    }

    @Test
    public void getPinnedItems() {
        given(listItemDao.getByUserId(USER_ID)).willReturn(Arrays.asList(listItem, listItem));
        given(listItem.isPinned()).willReturn(true)
            .willReturn(false);
        given(notebookViewFactory.create(listItem)).willReturn(notebookView);

        List<NotebookView> result = underTest.getPinnedItems(USER_ID, null);

        assertThat(result).containsExactly(notebookView);
        then(pinMappingDao).shouldHaveNoInteractions();
    }

    @Test
    public void getPinnedItems_notInGroup() {
        given(listItemDao.getByUserId(USER_ID)).willReturn(Arrays.asList(listItem, listItem));
        given(listItem.isPinned()).willReturn(true)
            .willReturn(false);
        given(pinMappingDao.getByPinGroupId(PIN_GROUP_ID)).willReturn(Collections.emptyList());

        List<NotebookView> result = underTest.getPinnedItems(USER_ID, PIN_GROUP_ID);

        assertThat(result).isEmpty();
    }

    @Test
    public void getPinnedItems_inGroup() {
        given(listItemDao.getByUserId(USER_ID)).willReturn(Arrays.asList(listItem, listItem));
        given(listItem.isPinned()).willReturn(true)
            .willReturn(false);
        given(notebookViewFactory.create(listItem)).willReturn(notebookView);
        given(pinMappingDao.getByPinGroupId(PIN_GROUP_ID)).willReturn(List.of(pinMapping));
        given(pinMapping.getListItemId()).willReturn(LIST_ITEM_ID);
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);

        List<NotebookView> result = underTest.getPinnedItems(USER_ID, PIN_GROUP_ID);

        assertThat(result).containsExactly(notebookView);
    }
}
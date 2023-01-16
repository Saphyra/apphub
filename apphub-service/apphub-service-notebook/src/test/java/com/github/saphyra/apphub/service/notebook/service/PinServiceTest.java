package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.api.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PinServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private NotebookViewFactory notebookViewFactory;

    @InjectMocks
    private PinService underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private NotebookView notebookView;

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

        List<NotebookView> result = underTest.getPinnedItems(USER_ID);

        assertThat(result).containsExactly(notebookView);
    }
}
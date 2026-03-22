package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ArchiveServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private ListItemDao listItemDao;

    @InjectMocks
    private ArchiveService underTest;

    @Mock
    private ListItem listItem;

    @Test
    public void nullValue() {
        Throwable ex = catchThrowable(() -> underTest.archive(LIST_ITEM_ID, null));

        ExceptionValidator.validateInvalidParam(ex, "archived", "must not be null");
    }

    @Test
    public void archive() {
        given(listItemDao.findByIdValidated(LIST_ITEM_ID)).willReturn(listItem);

        underTest.archive(LIST_ITEM_ID, true);

        verify(listItem).setArchived(true);
        verify(listItemDao).save(listItem);
    }
}
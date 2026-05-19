package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.api.feature.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItemDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ListItemQueryServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private DeprecatedListItemDao listItemDao;

    @Mock
    private NotebookViewFactory notebookViewFactory;

    @InjectMocks
    private ListItemQueryService underTest;

    @Mock
    private DeprecatedListItem listItem;

    @Mock
    private NotebookView notebookView;

    @Test
    void listItemNotFound() {
        given(listItemDao.findById(LIST_ITEM_ID)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.findListItem(LIST_ITEM_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void findListItem() {
        given(listItemDao.findById(LIST_ITEM_ID)).willReturn(Optional.of(listItem));
        given(notebookViewFactory.create(listItem)).willReturn(notebookView);

        assertThat(underTest.findListItem(LIST_ITEM_ID)).isEqualTo(notebookView);
    }
}
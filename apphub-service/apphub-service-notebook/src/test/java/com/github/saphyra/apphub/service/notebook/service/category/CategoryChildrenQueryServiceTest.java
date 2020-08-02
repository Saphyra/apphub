package com.github.saphyra.apphub.service.notebook.service.category;

import com.github.saphyra.apphub.api.notebook.model.response.ChildrenOfCategoryResponse;
import com.github.saphyra.apphub.api.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class CategoryChildrenQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CATEGORY_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID_1 = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID_2 = UUID.randomUUID();
    private static final String TITLE_1 = "title-1";
    private static final String TITLE_2 = "title-2";
    private static final UUID PARENT = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID_3 = UUID.randomUUID();

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private NotebookViewFactory notebookViewFactory;

    @InjectMocks
    private CategoryChildrenQueryService underTest;

    @Mock
    private NotebookView notebookView;

    @Test
    public void invalidType() {
        Throwable ex = catchThrowable(() -> underTest.getChildrenOfCategory(USER_ID, CATEGORY_ID, "a", null));

        assertThat(ex).isInstanceOf(BadRequestException.class);

        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams().get("type")).isEqualTo("contains invalid argument");
    }

    @Test
    public void emptyType() {
        ListItem listItem = ListItem.builder()
            .listItemId(LIST_ITEM_ID_1)
            .userId(USER_ID)
            .type(ListItemType.CATEGORY)
            .title(TITLE_1)
            .build();
        given(listItemDao.getByUserIdAndParent(USER_ID, CATEGORY_ID)).willReturn(Arrays.asList(listItem));
        given(notebookViewFactory.create(listItem)).willReturn(notebookView);

        ChildrenOfCategoryResponse result = underTest.getChildrenOfCategory(USER_ID, CATEGORY_ID, "", null);

        assertThat(result.getParent()).isNull();
        assertThat(result.getTitle()).isNull();
        assertThat(result.getChildren()).hasSize(1);
        assertThat(result.getChildren().get(0)).isEqualTo(notebookView);
    }

    @Test
    public void getRoot() {
        ListItem listItem1 = ListItem.builder()
            .listItemId(LIST_ITEM_ID_1)
            .userId(USER_ID)
            .type(ListItemType.CHECKLIST)
            .title(TITLE_1)
            .build();
        ListItem listItem2 = ListItem.builder()
            .listItemId(LIST_ITEM_ID_1)
            .userId(USER_ID)
            .type(ListItemType.CATEGORY)
            .title(TITLE_1)
            .build();
        given(listItemDao.getByUserIdAndParent(USER_ID, null)).willReturn(Arrays.asList(listItem1, listItem2));
        given(notebookViewFactory.create(listItem2)).willReturn(notebookView);

        ChildrenOfCategoryResponse result = underTest.getChildrenOfCategory(USER_ID, null, ListItemType.CATEGORY.name(), null);

        assertThat(result.getParent()).isNull();
        assertThat(result.getTitle()).isNull();
        assertThat(result.getChildren()).hasSize(1);
        assertThat(result.getChildren().get(0)).isEqualTo(notebookView);
    }

    @Test
    public void getChildrenOfCategory() {
        ListItem listItem1 = ListItem.builder()
            .listItemId(LIST_ITEM_ID_1)
            .userId(USER_ID)
            .type(ListItemType.CHECKLIST)
            .title(TITLE_1)
            .build();
        ListItem listItem2 = ListItem.builder()
            .listItemId(LIST_ITEM_ID_2)
            .userId(USER_ID)
            .type(ListItemType.CATEGORY)
            .title(TITLE_1)
            .build();
        ListItem listItem3 = ListItem.builder()
            .listItemId(LIST_ITEM_ID_3)
            .userId(USER_ID)
            .type(ListItemType.CATEGORY)
            .title(TITLE_1)
            .build();
        given(listItemDao.getByUserIdAndParent(USER_ID, CATEGORY_ID)).willReturn(Arrays.asList(listItem1, listItem2, listItem3));

        ListItem parent = ListItem.builder()
            .listItemId(CATEGORY_ID)
            .userId(USER_ID)
            .type(ListItemType.CATEGORY)
            .parent(PARENT)
            .title(TITLE_2)
            .build();
        given(listItemDao.findById(CATEGORY_ID)).willReturn(Optional.of(parent));
        given(notebookViewFactory.create(listItem2)).willReturn(notebookView);

        ChildrenOfCategoryResponse result = underTest.getChildrenOfCategory(USER_ID, CATEGORY_ID, ListItemType.CATEGORY.name(), LIST_ITEM_ID_3);

        assertThat(result.getParent()).isEqualTo(PARENT);
        assertThat(result.getTitle()).isEqualTo(TITLE_2);
        assertThat(result.getChildren()).hasSize(1);
        assertThat(result.getChildren().get(0)).isEqualTo(notebookView);
    }
}
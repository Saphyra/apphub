package com.github.saphyra.apphub.service.notebook.service.category;

import com.github.saphyra.apphub.api.notebook.model.response.CategoryTreeView;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class CategoryTreeQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID_1 = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID_2 = UUID.randomUUID();
    private static final String TITLE_1 = "title-1";
    private static final String TITLE_2 = "title-2";

    @Mock
    private ListItemDao listItemDao;

    @InjectMocks
    private CategoryTreeQueryService underTest;

    @Test
    public void getCategoryTree() {
        ListItem parent = ListItem.builder()
            .listItemId(LIST_ITEM_ID_1)
            .userId(USER_ID)
            .type(ListItemType.CATEGORY)
            .title(TITLE_1)
            .archived(true)
            .build();
        ListItem child = ListItem.builder()
            .listItemId(LIST_ITEM_ID_2)
            .userId(USER_ID)
            .type(ListItemType.CATEGORY)
            .title(TITLE_2)
            .parent(LIST_ITEM_ID_1)
            .archived(false)
            .build();
        given(listItemDao.getByUserIdAndType(USER_ID, ListItemType.CATEGORY)).willReturn(Arrays.asList(parent, child));

        List<CategoryTreeView> result = underTest.getCategoryTree(USER_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategoryId()).isEqualTo(LIST_ITEM_ID_1);
        assertThat(result.get(0).getTitle()).isEqualTo(TITLE_1);
        assertThat(result.get(0).getChildren()).hasSize(1);
        assertThat(result.get(0).isArchived()).isTrue();
        assertThat(result.get(0).getChildren().get(0).getCategoryId()).isEqualTo(LIST_ITEM_ID_2);
        assertThat(result.get(0).getChildren().get(0).getTitle()).isEqualTo(TITLE_2);
        assertThat(result.get(0).getChildren().get(0).getChildren()).isEmpty();
        assertThat(result.get(0).getChildren().get(0).isArchived()).isFalse();
    }
}
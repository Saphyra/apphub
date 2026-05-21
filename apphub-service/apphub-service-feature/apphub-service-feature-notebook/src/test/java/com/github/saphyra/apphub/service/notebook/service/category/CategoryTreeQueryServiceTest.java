package com.github.saphyra.apphub.service.notebook.service.category;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.response.CategoryTreeView;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
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
        assertThat(result.getFirst().getCategoryId()).isEqualTo(LIST_ITEM_ID_1);
        assertThat(result.getFirst().getTitle()).isEqualTo(TITLE_1);
        assertThat(result.getFirst().getChildren()).hasSize(1);
        assertThat(result.getFirst().isArchived()).isTrue();
        assertThat(result.getFirst().getChildren().getFirst().getCategoryId()).isEqualTo(LIST_ITEM_ID_2);
        assertThat(result.getFirst().getChildren().getFirst().getTitle()).isEqualTo(TITLE_2);
        assertThat(result.getFirst().getChildren().getFirst().getChildren()).isEmpty();
        assertThat(result.getFirst().getChildren().getFirst().isArchived()).isFalse();
    }
}
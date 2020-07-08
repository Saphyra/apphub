package com.github.saphyra.apphub.service.notebook.service.category;

import com.github.saphyra.apphub.api.notebook.model.response.CategoryTreeView;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class CategoryViewQueryService {
    private final ListItemDao listItemDao;

    public List<CategoryTreeView> getCategoryTree(UUID userId) {
        List<ListItem> listItems = listItemDao.getByUserIdAndType(userId, ListItemType.CATEGORY);

        return process(null, listItems);
    }

    private List<CategoryTreeView> process(UUID parent, List<ListItem> listItems) {
        return listItems.stream()
            .filter(listItem -> Objects.equals(parent, listItem.getParent()))
            .map(listItem -> CategoryTreeView.builder()
                .categoryId(listItem.getListItemId())
                .title(listItem.getTitle())
                .children(process(listItem.getListItemId(), listItems))
                .build())
            .collect(Collectors.toList());
    }
}
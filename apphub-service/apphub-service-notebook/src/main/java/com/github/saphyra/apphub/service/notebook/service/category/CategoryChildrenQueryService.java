package com.github.saphyra.apphub.service.notebook.service.category;

import com.github.saphyra.apphub.api.notebook.model.response.ChildrenOfCategoryResponse;
import com.github.saphyra.apphub.api.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.api.notebook.model.ListItemType;
import com.github.saphyra.apphub.service.notebook.service.NotebookViewFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class CategoryChildrenQueryService {
    private final ListItemDao listItemDao;
    private final NotebookViewFactory notebookViewFactory;

    public ChildrenOfCategoryResponse getChildrenOfCategory(UUID userId, UUID categoryId, String type, UUID exclude) {
        List<ListItemType> query = parseTypes(type);
        List<NotebookView> children = listItemDao.getByUserIdAndParent(userId, categoryId)
            .stream()
            .filter(listItem -> query.contains(listItem.getType()))
            .filter(listItem -> !listItem.getListItemId().equals(exclude))
            .map(notebookViewFactory::create)
            .collect(Collectors.toList());

        Optional<ListItem> category = Optional.ofNullable(categoryId)
            .flatMap(listItemDao::findById);
        return ChildrenOfCategoryResponse.builder()
            .parent(category.map(ListItem::getParent).orElse(null))
            .title(category.map(ListItem::getTitle).orElse(null))
            .listItemType(category.map(ListItem::getType).orElse(ListItemType.CATEGORY))
            .children(children)
            .build();
    }

    private List<ListItemType> parseTypes(String type) {
        try {
            return Optional.ofNullable(type)
                .filter(s -> !s.isEmpty())
                .map(s -> Arrays.stream(s.split(",")).map(ListItemType::valueOf).collect(Collectors.toList()))
                .orElseGet(() -> Arrays.stream(ListItemType.values())
                    .collect(Collectors.toList()));
        } catch (IllegalArgumentException e) {
            throw ExceptionFactory.invalidParam("type", "contains invalid argument");
        }
    }
}

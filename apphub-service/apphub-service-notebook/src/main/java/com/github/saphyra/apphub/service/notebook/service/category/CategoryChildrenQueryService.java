package com.github.saphyra.apphub.service.notebook.service.category;

import com.github.saphyra.apphub.api.notebook.model.response.ChildrenOfCategoryResponse;
import com.github.saphyra.apphub.api.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@Slf4j
@RequiredArgsConstructor
//TODO unit test
public class CategoryChildrenQueryService {
    private final ListItemDao listItemDao;

    public ChildrenOfCategoryResponse getChildrenOfCategory(UUID userId, UUID categoryId, String type) {
        List<ListItemType> query = parseTypes(type);
        List<NotebookView> children = listItemDao.getByUserIdAndParent(userId, categoryId)
            .stream()
            .filter(listItem -> query.contains(listItem.getType()))
            .map(listItem -> NotebookView.builder()
                .id(listItem.getListItemId())
                .title(listItem.getTitle())
                .type(listItem.getType().name())
                .build())
            .collect(Collectors.toList());
        return ChildrenOfCategoryResponse.builder()
            .parent(getParent(categoryId))
            .children(children)
            .build();
    }

    private UUID getParent(UUID categoryId) {
        return isNull(categoryId) ? null : listItemDao.findByIdValidated(categoryId).getParent();
    }

    private List<ListItemType> parseTypes(String type) {
        try {
            return Optional.ofNullable(type)
                .map(s -> Arrays.stream(s.split(",")).map(ListItemType::valueOf).collect(Collectors.toList()))
                .orElseGet(() -> Arrays.stream(ListItemType.values())
                    .collect(Collectors.toList()));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "type", "contains invalid argument"), type + "could not be converted to ListItemTypes");
        }
    }

    //TODO remove
    public List<ListItem> getItemsOfCategory(UUID userId, UUID categoryId) {
        return isNull(categoryId) ?
            listItemDao.getByUserIdAndParentIsNull(userId) :
            listItemDao.getByUserIdAndParent(userId, categoryId);
    }
}

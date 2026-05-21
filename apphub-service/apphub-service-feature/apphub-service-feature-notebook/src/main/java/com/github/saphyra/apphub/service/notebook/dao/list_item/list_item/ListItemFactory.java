package com.github.saphyra.apphub.service.notebook.dao.list_item.list_item;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ListItemFactory {
    private final IdGenerator idGenerator;

    public ListItem create(UUID userId, UUID parent, String title, ListItemType listItemType) {
        return create(userId, parent, title, listItemType, null);
    }

    public ListItem clone(UUID parent, ListItem listItem) {
        return create(listItem.getUserId(), parent, listItem.getTitle(), listItem.getType(), listItem.getData(), listItem.isPinned(), listItem.isArchived());
    }

    public ListItem clone(UUID parent, ListItem listItem, String data) {
        return create(listItem.getUserId(), parent, listItem.getTitle(), listItem.getType(), data, listItem.isPinned(), listItem.isArchived());
    }

    public ListItem create(UUID userId, UUID parent, String title, ListItemType listItemType, String data) {
        return create(userId, parent, title, listItemType, data, false, false);
    }

    public ListItem create(UUID userId, UUID parent, String title, ListItemType listItemType, String data, boolean pinned, boolean archived) {
        return ListItem.builder()
            .listItemId(idGenerator.randomUuid())
            .userId(userId)
            .parent(parent)
            .type(listItemType)
            .title(title)
            .pinned(pinned)
            .archived(archived)
            .data(data)
            .build();
    }
}

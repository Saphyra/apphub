package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ListItemFactory {
    private final IdGenerator idGenerator;

    public ListItem create(UUID userId, String title, UUID parent, ListItemType type) {
        return ListItem.builder()
            .listItemId(idGenerator.randomUUID())
            .userId(userId)
            .parent(parent)
            .type(type)
            .title(title)
            .build();
    }
}

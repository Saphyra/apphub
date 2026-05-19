package com.github.saphyra.apphub.service.notebook.dao.list_item;

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

    public ListItem create(UUID userId, UUID parent, String title, ListItemType listItemType, String data) {
        return ListItem.builder()
            .listItemId(idGenerator.randomUuid())
            .userId(userId)
            .parent(parent)
            .type(listItemType)
            .title(title)
            .pinned(false)
            .archived(false)
            .data(data)
            .build();
    }
}

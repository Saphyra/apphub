package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Deprecated(forRemoval = true)
public class DeprecatedListItemFactory {
    private final IdGenerator idGenerator;

    public DeprecatedListItem create(UUID userId, String title, UUID parent, ListItemType type) {
        return create(userId, title, parent, type, false, false);
    }

    public DeprecatedListItem create(UUID userId, String title, UUID parent, ListItemType type, boolean pinned, boolean archived) {
        return DeprecatedListItem.builder()
                .listItemId(idGenerator.randomUuid())
                .userId(userId)
                .parent(parent)
                .type(type)
                .title(title)
                .pinned(pinned)
                .archived(archived)
                .build();
    }
}

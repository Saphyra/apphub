package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_content.Content;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Deprecated(forRemoval = true)
public class DeprecatedContentFactory {
    private final IdGenerator idGenerator;

    public Content create(DeprecatedListItem listItem, String content) {
        return create(listItem.getListItemId(), listItem.getListItemId(), listItem.getUserId(), content);
    }

    public Content create(UUID listItemId, UUID parent, UUID userId, String content) {
        return Content.builder()
            .contentId(idGenerator.randomUuid())
            .userId(userId)
            .parent(parent)
            .listItemId(listItemId)
            .content(content)
            .build();
    }
}

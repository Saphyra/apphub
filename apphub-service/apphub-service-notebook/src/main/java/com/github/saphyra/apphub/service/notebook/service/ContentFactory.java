package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ContentFactory {
    private final IdGenerator idGenerator;

    public Content create(ListItem listItem, String content) {
        return create(listItem.getListItemId(), listItem.getUserId(), content);
    }

    //TODO unit test
    public Content create(UUID parent, UUID userId, String content) {
        return Content.builder()
            .contentId(idGenerator.randomUUID())
            .userId(userId)
            .parent(parent)
            .content(content)
            .build();
    }
}

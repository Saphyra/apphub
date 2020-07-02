package com.github.saphyra.apphub.service.notebook.service.category.creation;

import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import com.github.saphyra.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
public class CategoryFactory {
    private final IdGenerator idGenerator;

    public ListItem create(UUID userId, String title, UUID parent) {
        return ListItem.builder()
            .listItemId(idGenerator.randomUUID())
            .userId(userId)
            .parent(parent)
            .type(ListItemType.CATEGORY)
            .title(title)
            .build();
    }
}

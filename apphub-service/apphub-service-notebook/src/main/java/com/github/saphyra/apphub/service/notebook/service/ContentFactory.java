package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContentFactory {
    private final IdGenerator idGenerator;

    public Content create(ListItem listItem, String content){
        return  Content.builder()
            .contentId(idGenerator.randomUUID())
            .userId(listItem.getUserId())
            .parent(listItem.getListItemId())
            .content(content)
            .build();
    }
}

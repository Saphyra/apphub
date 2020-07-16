package com.github.saphyra.apphub.service.notebook.service.text.creation;

import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.text.Text;
import com.github.saphyra.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class TextFactory {
    private final IdGenerator idGenerator;

    Text create(ListItem listItem, String content){
        return  Text.builder()
            .textId(idGenerator.randomUUID())
            .userId(listItem.getUserId())
            .parent(listItem.getListItemId())
            .content(content)
            .build();
    }
}

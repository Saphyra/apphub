package com.github.saphyra.apphub.service.notebook.dao.list_item.content;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContentFactory {
    public Content create(UUID listItemId, UUID key, String content) {
        return Content.builder()
            .listItemId(listItemId)
            .build()
            .add(key, content);
    }

    public Content create(UUID listItemId, int batchIndex) {
        return Content.builder()
            .listItemId(listItemId)
            .batchIndex(batchIndex)
            .build();
    }
}

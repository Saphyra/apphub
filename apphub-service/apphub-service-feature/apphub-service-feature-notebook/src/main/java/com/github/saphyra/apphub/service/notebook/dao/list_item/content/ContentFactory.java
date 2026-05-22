package com.github.saphyra.apphub.service.notebook.dao.list_item.content;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ContentFactory {
    private final UuidConverter uuidConverter;

    public Content create(UUID listItemId, UUID key, String content) {
        return Content.builder()
            .listItemId(listItemId)
            .build()
            .add(uuidConverter.convertDomain(key), content);
    }

    public Content create(UUID listItemId, int batchIndex) {
        return Content.builder()
            .listItemId(listItemId)
            .batchIndex(batchIndex)
            .build();
    }
}

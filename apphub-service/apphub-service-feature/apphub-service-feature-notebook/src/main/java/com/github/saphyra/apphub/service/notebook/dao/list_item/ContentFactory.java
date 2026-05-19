package com.github.saphyra.apphub.service.notebook.dao.list_item;

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

    public Content create(UUID userId, UUID listItemId, ParentType parentType, UUID key, String content) {
        return Content.builder()
            .userId(userId)
            .listItemId(listItemId)
            .parentType(parentType)
            .build()
            .add(uuidConverter.convertDomain(key), content);
    }
}

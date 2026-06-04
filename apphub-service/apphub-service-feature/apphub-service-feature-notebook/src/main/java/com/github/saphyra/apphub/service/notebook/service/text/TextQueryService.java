package com.github.saphyra.apphub.service.notebook.service.text;

import com.github.saphyra.apphub.api.feature.notebook.model.response.TextResponse;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Component
public class TextQueryService {
    private final ListItemDao listItemDao;

    public TextResponse getTextResponse(UUID userId, UUID listItemId) {
        ListItem listItem = listItemDao.findByIdValidated(userId, listItemId);

        return TextResponse.builder()
            .textId(listItemId)
            .parent(listItem.getParent())
            .title(listItem.getTitle())
            .content(listItem.getData())
            .build();
    }
}

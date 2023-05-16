package com.github.saphyra.apphub.service.notebook.service.text;

import com.github.saphyra.apphub.api.notebook.model.response.TextResponse;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Component
//TODO unit test
public class TextQueryService {
    private final ListItemDao listItemDao;
    private final ContentDao contentDao;

    public TextResponse getTextResponse(UUID textId) {
        ListItem listItem = listItemDao.findByIdValidated(textId);
        Content content = contentDao.findByParentValidated(textId);

        return TextResponse.builder()
            .textId(textId)
            .parent(listItem.getParent())
            .title(listItem.getTitle())
            .content(content.getContent())
            .build();
    }
}

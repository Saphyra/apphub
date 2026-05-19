package com.github.saphyra.apphub.service.notebook.service.text;

import com.github.saphyra.apphub.api.feature.notebook.model.response.TextResponse;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_content.Content;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_content.ContentDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Component
public class TextQueryService {
    private final DeprecatedListItemDao listItemDao;
    private final ContentDao contentDao;

    public TextResponse getTextResponse(UUID textId) {
        DeprecatedListItem listItem = listItemDao.findByIdValidated(textId);
        Content content = contentDao.findByParentValidated(textId);

        return TextResponse.builder()
            .textId(textId)
            .parent(listItem.getParent())
            .title(listItem.getTitle())
            .content(content.getContent())
            .build();
    }
}

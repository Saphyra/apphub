package com.github.saphyra.apphub.service.notebook.service.text.creation;

import com.github.saphyra.apphub.api.feature.notebook.model.request.CreateTextRequest;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItemDao;
import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_content.Content;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_content.ContentDao;
import com.github.saphyra.apphub.service.notebook.service.ContentFactory;
import com.github.saphyra.apphub.service.notebook.service.DeprecatedListItemFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TextCreationService {
    private final CreateTextRequestValidator createTextRequestValidator;
    private final DeprecatedListItemDao listItemDao;
    private final DeprecatedListItemFactory listItemFactory;
    private final ContentDao contentDao;
    private final ContentFactory contentFactory;

    @Transactional
    public UUID create(CreateTextRequest request, UUID userId) {
        createTextRequestValidator.validate(request);

        DeprecatedListItem listItem = listItemFactory.create(userId, request.getTitle(), request.getParent(), ListItemType.TEXT);
        Content content = contentFactory.create(listItem, request.getContent());

        listItemDao.save(listItem);
        contentDao.save(content);
        return listItem.getListItemId();
    }
}

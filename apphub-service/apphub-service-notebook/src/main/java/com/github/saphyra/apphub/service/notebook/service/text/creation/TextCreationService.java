package com.github.saphyra.apphub.service.notebook.service.text.creation;

import com.github.saphyra.apphub.api.notebook.model.request.CreateTextRequest;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.api.notebook.model.ListItemType;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.service.ContentFactory;
import com.github.saphyra.apphub.service.notebook.service.ListItemFactory;
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
    private final ListItemDao listItemDao;
    private final ListItemFactory listItemFactory;
    private final ContentDao contentDao;
    private final ContentFactory contentFactory;

    @Transactional
    public UUID create(CreateTextRequest request, UUID userId) {
        createTextRequestValidator.validate(request);

        ListItem listItem = listItemFactory.create(userId, request.getTitle(), request.getParent(), ListItemType.TEXT);
        Content content = contentFactory.create(listItem, request.getContent());

        listItemDao.save(listItem);
        contentDao.save(content);
        return listItem.getListItemId();
    }
}

package com.github.saphyra.apphub.service.notebook.service.link;

import com.github.saphyra.apphub.api.notebook.model.request.LinkRequest;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.api.notebook.model.ListItemType;
import com.github.saphyra.apphub.service.notebook.service.ContentFactory;
import com.github.saphyra.apphub.service.notebook.service.ListItemFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class LinkCreationService {
    private final ContentDao contentDao;
    private final ContentFactory contentFactory;
    private final LinkRequestValidator linkRequestValidator;
    private final ListItemDao listItemDao;
    private final ListItemFactory listItemFactory;

    public UUID create(LinkRequest request, UUID userId) {
        linkRequestValidator.validate(request);

        ListItem listItem = listItemFactory.create(userId, request.getTitle(), request.getParent(), ListItemType.LINK);
        Content content = contentFactory.create(listItem, request.getUrl());

        listItemDao.save(listItem);
        contentDao.save(content);
        return listItem.getListItemId();
    }
}

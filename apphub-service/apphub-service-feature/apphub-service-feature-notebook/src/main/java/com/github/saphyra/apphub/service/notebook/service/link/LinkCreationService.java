package com.github.saphyra.apphub.service.notebook.service.link;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.request.LinkRequest;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class LinkCreationService {
    private final LinkRequestValidator linkRequestValidator;
    private final ListItemDao listItemDao;
    private final ListItemFactory listItemFactory;

    public UUID create(LinkRequest request, UUID userId) {
        linkRequestValidator.validate(userId, request);

        ListItem listItem = listItemFactory.create(userId, request.getParent(), request.getTitle(), ListItemType.LINK, request.getUrl());

        listItemDao.save(listItem);

        return listItem.getListItemId();
    }
}

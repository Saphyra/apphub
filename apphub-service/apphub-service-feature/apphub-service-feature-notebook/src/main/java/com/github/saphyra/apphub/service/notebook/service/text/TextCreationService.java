package com.github.saphyra.apphub.service.notebook.service.text;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.request.CreateTextRequest;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemFactory;
import com.github.saphyra.apphub.service.notebook.service.validator.TextValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TextCreationService {
    private final TextValidator textValidator;
    private final ListItemDao listItemDao;
    private final ListItemFactory listItemFactory;

    @Transactional
    public UUID create(CreateTextRequest request, UUID userId) {
        textValidator.validate(userId, request);

        ListItem listItem = listItemFactory.create(userId,  request.getParent(), request.getTitle(),ListItemType.TEXT, request.getContent());

        listItemDao.save(listItem);

        return listItem.getListItemId();
    }
}

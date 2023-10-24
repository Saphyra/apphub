package com.github.saphyra.apphub.service.notebook.service.only_title;

import com.github.saphyra.apphub.api.notebook.model.request.CreateOnlyTitleRequest;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.api.notebook.model.ListItemType;
import com.github.saphyra.apphub.service.notebook.service.ListItemFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OnlyTitleCreationService {
    private final CreateOnlyTitleRequestValidator createOnlyTitleRequestValidator;
    private final ListItemFactory listItemFactory;
    private final ListItemDao listItemDao;

    public UUID create(CreateOnlyTitleRequest request, UUID userId) {
        createOnlyTitleRequestValidator.validate(request);

        ListItem listItem = listItemFactory.create(userId, request.getTitle(), request.getParent(), ListItemType.ONLY_TITLE);
        listItemDao.save(listItem);
        return listItem.getListItemId();
    }
}

package com.github.saphyra.apphub.service.notebook.service.text;

import com.github.saphyra.apphub.api.feature.notebook.model.request.EditTextRequest;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.service.validator.TextValidator;
import com.github.saphyra.apphub.service.notebook.service.validator.TitleValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class EditTextService {
    private final TextValidator textValidator;
    private final TitleValidator titleValidator;
    private final ListItemDao listItemDao;

    public void editText(UUID userId, UUID listItemId, EditTextRequest request) {
        titleValidator.validate(request.getTitle());
        textValidator.validate(request.getContent(), "content");

        ListItem listItem = listItemDao.findByIdValidated(userId, listItemId);

        listItem.setTitle(request.getTitle());
        listItem.setData(request.getContent());

        listItemDao.saveListItem(listItem);
    }
}

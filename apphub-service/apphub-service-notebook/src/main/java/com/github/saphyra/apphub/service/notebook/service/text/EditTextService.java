package com.github.saphyra.apphub.service.notebook.service.text;

import com.github.saphyra.apphub.api.notebook.model.request.EditTextRequest;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.service.TitleValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class EditTextService {
    private final ContentValidator contentValidator;
    private final TitleValidator titleValidator;
    private final ListItemDao listItemDao;
    private final ContentDao contentDao;

    @Transactional
    public void editText(UUID textId, EditTextRequest request) {
        titleValidator.validate(request.getTitle());
        contentValidator.validate(request.getContent(), "content");

        ListItem listItem = listItemDao.findByIdValidated(textId);
        Content content = contentDao.findByParentValidated(textId);

        listItem.setTitle(request.getTitle());
        content.setContent(request.getContent());

        listItemDao.save(listItem);
        contentDao.save(content);
    }
}

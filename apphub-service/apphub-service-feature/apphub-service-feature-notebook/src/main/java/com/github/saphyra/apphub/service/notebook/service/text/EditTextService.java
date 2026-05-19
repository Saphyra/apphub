package com.github.saphyra.apphub.service.notebook.service.text;

import com.github.saphyra.apphub.api.feature.notebook.model.request.EditTextRequest;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_content.Content;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_content.ContentDao;
import com.github.saphyra.apphub.service.notebook.service.validator.TitleValidator;
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
    private final DeprecatedListItemDao listItemDao;
    private final ContentDao contentDao;

    @Transactional
    public void editText(UUID textId, EditTextRequest request) {
        titleValidator.validate(request.getTitle());
        contentValidator.validate(request.getContent(), "content");

        DeprecatedListItem listItem = listItemDao.findByIdValidated(textId);
        Content content = contentDao.findByParentValidated(textId);

        listItem.setTitle(request.getTitle());
        content.setContent(request.getContent());

        listItemDao.save(listItem);
        contentDao.save(content);
    }
}

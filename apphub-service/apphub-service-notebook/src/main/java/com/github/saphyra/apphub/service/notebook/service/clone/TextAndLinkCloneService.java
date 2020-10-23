package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.service.ContentFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class TextAndLinkCloneService {
    private final ContentDao contentDao;
    private final ContentFactory contentFactory;

    void clone(UUID originalParent, ListItem cloneParent) {
        Content content = contentDao.findByParentValidated(originalParent);
        Content clone = contentFactory.create(cloneParent, content.getContent());
        contentDao.save(clone);
    }
}

package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChecklistItemContentUpdateService {
    private final ContentDao contentDao;

    public void updateContent(UUID checklistItemId, String contentString) {
        ValidationUtil.notNull(contentString, "content");

        Content content = contentDao.findByParentValidated(checklistItemId);
        content.setContent(contentString);
        contentDao.save(content);
    }
}

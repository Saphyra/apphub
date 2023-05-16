package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.api.notebook.model.request.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.service.text.ContentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class ChecklistItemNodeRequestValidator {
    private final ChecklistItemDao checklistItemDao;
    private final ContentValidator contentValidator;
    private final UuidConverter uuidConverter;

    public void validate(ChecklistItemNodeRequest request) {
        contentValidator.validate(request.getContent(), "node.content");

        if (isNull(request.getChecked())) {
            throw ExceptionFactory.invalidParam("node.checked", "must not be null");
        }

        if (isNull(request.getOrder())) {
            throw ExceptionFactory.invalidParam("node.order", "must not be null");
        }

        if (!isNull(request.getChecklistItemId())) {
            if (checklistItemDao.findById(uuidConverter.convertDomain(request.getChecklistItemId())).isEmpty()) {
                throw ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.LIST_ITEM_NOT_FOUND, "ChecklistListItem not found with id " + request.getChecklistItemId());
            }
        }
    }
}

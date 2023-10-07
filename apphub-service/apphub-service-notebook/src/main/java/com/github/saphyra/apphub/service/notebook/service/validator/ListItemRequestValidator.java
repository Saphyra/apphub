package com.github.saphyra.apphub.service.notebook.service.validator;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class ListItemRequestValidator {
    private final ListItemDao listItemDao;
    private final TitleValidator titleValidator;

    public void validate(String title, UUID parent) {
        titleValidator.validate(title);

        if (!isNull(parent)) {
            Optional<ListItem> listItem = listItemDao.findById(parent);
            if (listItem.isEmpty()) {
                throw ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.CATEGORY_NOT_FOUND, "Category not found with id " + parent);
            }

            if (listItem.get().getType() != ListItemType.CATEGORY) {
                throw ExceptionFactory.invalidType("Parent is not a CATEGORY, it is " + listItem.get().getType());
            }
        }
    }
}

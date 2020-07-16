package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import com.github.saphyra.apphub.lib.exception.NotFoundException;
import com.github.saphyra.apphub.lib.exception.UnprocessableEntityException;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;
import static org.apache.commons.lang.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ListItemRequestValidator {
    private final ListItemDao listItemDao;

    public void validate(String title, UUID parent) {
        if (isBlank(title)) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "title", "must not be null or blank"), "Title must not be null or blank");
        }

        if (!isNull(parent)) {
            Optional<ListItem> listItem = listItemDao.findById(parent);
            if (!listItem.isPresent()) {
                throw new NotFoundException(new ErrorMessage(ErrorCode.CATEGORY_NOT_FOUND.name()), "Category not found with listItemId " + parent);
            }

            if (listItem.get().getType() != ListItemType.CATEGORY) {
                throw new UnprocessableEntityException(new ErrorMessage(ErrorCode.INVALID_TYPE.name()), "Parent is not a CATEGORY, it is " + listItem.get().getType());
            }
        }
    }
}

package com.github.saphyra.apphub.service.notebook.dao.list_item;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import com.github.saphyra.apphub.lib.exception.NotFoundException;
import com.github.saphyra.dao.AbstractDao;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ListItemDao extends AbstractDao<ListItemEntity, ListItem, String, ListItemRepository> {
    private final UuidConverter uuidConverter;

    public ListItemDao(ListItemConverter converter, ListItemRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public List<ListItem> getByUserIdAndType(UUID userId, ListItemType type) {
        return converter.convertEntity(repository.getByUserIdAndType(uuidConverter.convertDomain(userId), type));
    }

    public Optional<ListItem> findById(UUID listItemId) {
        return findById(uuidConverter.convertDomain(listItemId));
    }

    public ListItem findByIdValidated(UUID listItemId) {
        return findById(listItemId)
            .orElseThrow(() -> new NotFoundException(new ErrorMessage(ErrorCode.LIST_ITEM_NOT_FOUND.name()), "ListItem not found with id " + listItemId));
    }

    //UserId is necessary because of the root's children, since parent is null for multiple users' records
    public List<ListItem> getByUserIdAndParent(UUID userId, UUID parent) {
        return converter.convertEntity(repository.getByUserIdAndParent(
            uuidConverter.convertDomain(userId),
            uuidConverter.convertDomain(parent)
        ));
    }

    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }
}

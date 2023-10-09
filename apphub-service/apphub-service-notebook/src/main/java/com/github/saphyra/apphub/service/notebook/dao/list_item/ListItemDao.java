package com.github.saphyra.apphub.service.notebook.dao.list_item;

import com.github.saphyra.apphub.api.notebook.model.ListItemType;
import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ListItemDao extends AbstractDao<ListItemEntity, ListItem, String, ListItemRepository> implements DeleteByUserIdDao {
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
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.LIST_ITEM_NOT_FOUND, "ListItem not found with id " + listItemId));
    }

    //UserId is necessary because of the root's children, since parent is null for multiple users' records
    public List<ListItem> getByUserIdAndParent(UUID userId, UUID parent) {
        return converter.convertEntity(repository.getByUserIdAndParent(
            uuidConverter.convertDomain(userId),
            uuidConverter.convertDomain(parent)
        ));
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public List<ListItem> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }
}

package com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Deprecated(forRemoval = true)
public class DeprecatedListItemDao extends AbstractDao<ListItemEntity, DeprecatedListItem, String, DeprecatedListItemRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;
    private final DeprecatedListItemConverter converter;

    public DeprecatedListItemDao(DeprecatedListItemConverter converter, DeprecatedListItemRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
        this.converter = converter;
    }

    public List<DeprecatedListItem> getByUserIdAndType(UUID userId, ListItemType type) {
        return converter.convertEntity(repository.getByUserIdAndType(uuidConverter.convertDomain(userId), type));
    }

    public Optional<DeprecatedListItem> findById(UUID listItemId) {
        return findById(uuidConverter.convertDomain(listItemId));
    }

    public DeprecatedListItem findByIdValidated(UUID listItemId) {
        return findById(listItemId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.LIST_ITEM_NOT_FOUND, "ListItem not found with id " + listItemId));
    }

    //UserId is necessary because of the root's children, since parent is null for multiple users' records
    public List<DeprecatedListItem> getByUserIdAndParent(UUID userId, UUID parent) {
        return converter.convertEntity(repository.getByUserIdAndParent(
            uuidConverter.convertDomain(userId),
            uuidConverter.convertDomain(parent)
        ));
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public List<DeprecatedListItem> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }
}

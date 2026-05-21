package com.github.saphyra.apphub.service.notebook.dao.list_item.list_item;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.github.saphyra.apphub.lib.common_domain.Constants.DYNAMO_DB_QUERY_MAX_BATCH_SIZE;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ListItemDao {
    private final ListItemConverter converter;
    private final ListItemRepository repository;
    private final UuidConverter uuidConverter;

    public void saveListItem(ListItem listItem) {
        repository.save(converter.convertDomain(listItem));
    }

    public List<ListItem> getByUserIdAndParent(UUID userId, UUID parent) {
        return converter.convertEntity(repository.getByUserIdAndParent(
            uuidConverter.convertDomain(userId),
            uuidConverter.convertDomain(parent)
        ));
    }

    public List<ListItem> getByUserIdAndType(UUID userId, ListItemType listItemType) {
        return converter.convertEntity(repository.getByUserIdAndType(
            uuidConverter.convertDomain(userId),
            listItemType.name()
        ));
    }

    public Optional<ListItem> findById(UUID userId, UUID listItemId) {
        return converter.convertEntity(repository.findById(
            uuidConverter.convertDomain(userId),
            uuidConverter.convertDomain(listItemId)
        ));
    }

    public ListItem findByIdValidated(UUID userId, UUID listItemId) {
        return findById(userId, listItemId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.LIST_ITEM_NOT_FOUND, "ListItem not found with id " + listItemId));
    }

    public List<ListItem> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }

    public List<ListItem> getByIds(UUID userId, List<UUID> listItemIds) {
        String userIdString = uuidConverter.convertDomain(userId);

        return Lists.partition(uuidConverter.convertDomain(listItemIds), DYNAMO_DB_QUERY_MAX_BATCH_SIZE)
            .stream()
            .flatMap(partition -> repository.getByIds(userIdString, partition).stream())
            .map(converter::convertEntity)
            .toList();
    }

    public void delete(ListItem listItem) {
        repository.delete(uuidConverter.convertDomain(listItem.getUserId()), uuidConverter.convertDomain(listItem.getListItemId()));
    }
}

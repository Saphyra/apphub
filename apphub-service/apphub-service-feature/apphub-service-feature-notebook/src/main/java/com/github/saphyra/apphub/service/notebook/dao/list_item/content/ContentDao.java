package com.github.saphyra.apphub.service.notebook.dao.list_item.content;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ContentDao {
    private final ContentRepository repository;
    private final ContentConverter converter;
    private final UuidConverter uuidConverter;
    private final ContentAggregator contentAggregator;

    public List<Content> getContentsByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }

    public List<Content> getByListItemIdAndType(UUID userId, UUID listItemId, ParentType parentType) {
        return converter.convertEntity(repository.getByListItemIdAndType(
            uuidConverter.convertDomain(userId),
            uuidConverter.convertDomain(listItemId),
            parentType.name()
        ));
    }

    public void save(UUID userId, UUID listItemId, List<Content> contents) {
        List<Content> toSave = contentAggregator.aggregate(userId, listItemId, contents);

        Lists.partition(toSave, Constants.DYNAMO_DB_INSERT_MAX_BATCH_SIZE)
            .stream()
            .map(converter::convertDomain)
            .forEach(repository::save);
    }

    /**
     * Deletes the given entry from a {@link Content}.
     * <p>
     * If the content does not have any more entries left, delete the record itself
     */
    public void delete(UUID userId, UUID listItemId, UUID key, ParentType parentType) {
        delete(userId, listItemId, List.of(key), parentType);
    }

    /**
     * Deletes the given entries from {@link Content}s.
     * <p>
     * If a content does not have any more entries left, delete the record itself
     */
    public void delete(UUID userId, UUID listItemId, List<UUID> keys, ParentType parentType) {
        List<String> keysString = uuidConverter.convertDomain(keys);

        List<Content> modified = getByListItemIdAndType(userId, listItemId, parentType)
            .stream()
            .filter(c -> c.containsAny(keysString))
            .peek(content -> content.removeAll(keysString))
            .filter(Content::isModified)
            .toList();

        List<Content> toSave = modified.stream()
            .filter(content -> !content.getContent().isEmpty())
            .toList();
        List<Content> toDelete = modified.stream()
            .filter(content -> content.getContent().isEmpty())
            .toList();

        save(userId, listItemId, toSave);
        delete(userId, listItemId, toDelete);
    }

    /**
     * Delete the provided content records
     */
    public void delete(UUID userId, UUID listItemId, List<Content> contents) {
        List<BiWrapper<String, Integer>> ids = contents.stream()
            .map(content -> new BiWrapper<>(content.getParentType().name(), content.getBatchIndex()))
            .toList();

        String userIdString = uuidConverter.convertDomain(userId);
        String listItemIdString = uuidConverter.convertDomain(listItemId);

        Lists.partition(ids, Constants.DYNAMO_DB_DELETE_MAX_BATCH_SIZE)
            .forEach(i -> repository.delete(userIdString, listItemIdString, i));
    }
}

package com.github.saphyra.apphub.service.notebook.dao.list_item.content;

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

    public List<Content> getByListItemId(UUID listItemId) {
        return converter.convertEntity(repository.getByListItemId(uuidConverter.convertDomain(listItemId)));
    }

    public void save(UUID listItemId, List<Content> contents) {
        //TODO think about deleting empty contents
        List<Content> toSave = contentAggregator.aggregate(listItemId, contents);

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
    public void delete(UUID listItemId, UUID key) {
        deleteKeys(listItemId, List.of(key));
    }

    /**
     * Deletes the given entries from {@link Content}s.
     * <p>
     * If a content does not have any more entries left, delete the record itself
     */
    public void deleteKeys(UUID listItemId, List<UUID> keys) {
        List<Content> modified = getByListItemId(listItemId)
            .stream()
            .filter(c -> c.containsAny(keys))
            .peek(content -> content.removeAll(keys))
            .filter(Content::isModified)
            .toList();

        List<Content> toSave = modified.stream()
            .filter(content -> !content.getContent().isEmpty())
            .toList();
        List<Content> toDelete = modified.stream()
            .filter(content -> content.getContent().isEmpty())
            .toList();

        save(listItemId, toSave);
        delete(listItemId, toDelete);
    }

    /**
     * Delete the provided content records
     */
    public void delete(UUID listItemId, List<Content> contents) {
        List<Integer> batchIndexes = contents.stream()
            .map(Content::getBatchIndex)
            .toList();

        String listItemIdString = uuidConverter.convertDomain(listItemId);

        Lists.partition(batchIndexes, Constants.DYNAMO_DB_DELETE_MAX_BATCH_SIZE)
            .forEach(i -> repository.delete(listItemIdString, i));
    }

    public void delete(UUID listItemId) {
        delete(
            listItemId,
            getByListItemId(listItemId)
        );
    }
}

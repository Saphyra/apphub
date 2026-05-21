package com.github.saphyra.apphub.service.notebook.dao.list_item.content;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
//TODO unit test
class ContentAggregator {
    private static final int SOFT_CAP = 100 * 1024;

    private final ContentFactory contentFactory;

    /**
     * Aggregates {@link Content}s in the following way:
     * <ol>
     *     <li>Every content must be assigned to a record with a batchIndex</li>
     *     <li>New records are placed into already modified records if any, or to an unmodified record, or new batchIndex is created as last resort</li>
     *     <li>Put into existing record until it fits in SOFT_CAP</li>
     * </ol>
     */
    List<Content> aggregate(UUID userId, UUID listItemId, List<Content> contents) {
        List<Content> toPlace = contents.stream()
            .filter(content -> isNull(content.getBatchIndex()))
            .collect(Collectors.toCollection(ArrayList::new));

        List<Content> modified = contents.stream()
            .filter(content -> nonNull(content.getBatchIndex()))
            .filter(Content::isModified)
            .collect(Collectors.toCollection(ArrayList::new));

        if (!toPlace.isEmpty()) {
            List<Content> unmodified = contents.stream()
                .filter(content -> nonNull(content.getBatchIndex()))
                .filter(content -> !content.isModified())
                .collect(Collectors.toCollection(ArrayList::new));

            toPlace.forEach(content -> {
                ParentType parentType = content.getParentType();
                content.getContent()
                    .forEach((key, value) -> place(userId, listItemId, parentType, key, value, modified, unmodified));
            });
        }

        return modified;
    }

    private void place(UUID userId, UUID listItemId, ParentType parentType, String key, String value, List<Content> modified, List<Content> unmodified) {
        Content container = findContainer(userId, listItemId, parentType, value, modified, unmodified);

        container.add(key, value);
    }

    private Content findContainer(UUID userId, UUID listItemId, ParentType parentType, String value, List<Content> modified, List<Content> unmodified) {
        return findContainer(value, modified)
            .or(() -> {
                Optional<Content> c = findContainer(value, unmodified);
                c.ifPresent(content -> {
                    unmodified.remove(content);
                    modified.add(content);
                });
                return c;
            })
            .orElseGet(() -> {
                Content content = createContainer(userId, listItemId, parentType, modified, unmodified);
                modified.add(content);
                return content;
            });
    }

    private Content createContainer(UUID userId, UUID listItemId, ParentType parentType, List<Content> modified, List<Content> unmodified) {
        int batchIndex = getBatchIndex(modified, unmodified);


        return contentFactory.create(userId, listItemId, parentType, batchIndex);
    }

    private int getBatchIndex(List<Content> modified, List<Content> unmodified) {
        Set<Integer> batchIndexes = Stream.concat(modified.stream(), unmodified.stream())
            .map(Content::getBatchIndex)
            .collect(Collectors.toSet());

        for (int i = 0; ; i++) {
            if (!batchIndexes.contains(i)) {
                return i;
            }
        }
    }

    private Optional<Content> findContainer(String value, List<Content> contents) {
        int closestCapacity = Integer.MAX_VALUE;
        Optional<Content> result = Optional.empty();

        for (Content content : contents) {
            int availableCapacity = SOFT_CAP - content.getSize();
            if (availableCapacity > 0 && availableCapacity >= value.length() && availableCapacity < closestCapacity) {
                closestCapacity = availableCapacity;
                result = Optional.of(content);
            }
        }

        return result;
    }
}

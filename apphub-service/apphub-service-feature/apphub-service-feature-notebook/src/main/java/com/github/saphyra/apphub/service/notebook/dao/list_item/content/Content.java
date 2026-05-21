package com.github.saphyra.apphub.service.notebook.dao.list_item.content;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static java.util.Objects.nonNull;

/**
 * New: batchId null, repository assign it to an existing batch if fits or new one if not
 * Delete: delete text from existing batch, and mark it as modified
 * Edit: delete and add new
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
//TODO unit test
public class Content {
    @NonNull
    private UUID userId;
    @NonNull
    private UUID listItemId;
    @Nullable //Null when new
    private Integer batchIndex;
    @NonNull
    private ParentType parentType;
    @Builder.Default
    private Map<String, String> content = new HashMap<>(); //TODO change key to UUID if no String stored
    private boolean modified;

    public Content add(String key, String value) {
        content.put(key, value);

        modified = true;

        return this;
    }

    public Content remove(String key) {
        if (nonNull(content.remove(key))) {
            modified = true;
        }

        return this;
    }

    public String get(String key) {
        return content.get(key);
    }

    public boolean contains(String key) {
        return content.containsKey(key);
    }

    public int getSize() {
        return content.entrySet()
            .stream()
            .mapToInt(entry -> entry.getKey().length() + entry.getValue().length())
            .sum();
    }

    public boolean containsAny(List<String> keysString) {
        return content.keySet()
            .stream()
            .anyMatch(keysString::contains);
    }

    public void removeAll(List<String> keysString) {
        boolean modified = !keysString.stream()
            .map(content::remove)
            .filter(Objects::nonNull)
            .toList()
            .isEmpty();

        if (modified) {
            this.modified = true;
        }
    }
}

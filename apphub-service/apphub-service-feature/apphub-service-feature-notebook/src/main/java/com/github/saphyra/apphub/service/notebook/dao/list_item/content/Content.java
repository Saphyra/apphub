package com.github.saphyra.apphub.service.notebook.dao.list_item.content;

import com.github.saphyra.apphub.lib.common_domain.Constants;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;

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
@ToString(exclude = "content")
public class Content {
    @NonNull
    private UUID listItemId;
    @Nullable //Null when new
    private Integer batchIndex;
    @Builder.Default
    private Map<UUID, String> content = new HashMap<>();
    private boolean modified;

    public Content add(UUID key, String value) {
        content.put(key, value);

        modified = true;

        return this;
    }

    public void remove(UUID key) {
        if (nonNull(content.remove(key))) {
            modified = true;
        }
    }

    public String get(UUID key) {
        return content.get(key);
    }

    public boolean contains(UUID key) {
        return content.containsKey(key);
    }

    public int getSize() {
        return content.values()
            .stream()
            .mapToInt(s -> Constants.UUID_LENGTH + s.length())
            .sum();
    }

    public boolean containsAny(List<UUID> keysString) {
        return content.keySet()
            .stream()
            .anyMatch(keysString::contains);
    }

    public boolean removeAll(List<UUID> keysString) {
        boolean modified = !keysString.stream()
            .map(content::remove)
            .filter(Objects::nonNull)
            .toList()
            .isEmpty();

        if (modified) {
            this.modified = true;
        }

        return modified;
    }
}

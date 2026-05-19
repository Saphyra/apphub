package com.github.saphyra.apphub.service.notebook.dao.list_item;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
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
        content.remove(key);

        modified = true;

        return this;
    }
}

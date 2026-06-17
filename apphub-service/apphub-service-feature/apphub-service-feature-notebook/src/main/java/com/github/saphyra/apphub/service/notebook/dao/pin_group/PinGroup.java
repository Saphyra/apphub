package com.github.saphyra.apphub.service.notebook.dao.pin_group;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class PinGroup {
    @NonNull
    private final UUID userId;

    @NonNull
    private final UUID pinGroupId;

    @NonNull
    private String pinGroupName;

    @NonNull
    private LocalDateTime lastOpened;

    @NonNull
    @Builder.Default
    private final Set<UUID> listItemIds = new HashSet<>();

    public void addListItem(UUID listItemId) {
        listItemIds.add(listItemId);
    }

    public void removeListItem(UUID listItemId) {
        listItemIds.remove(listItemId);
    }
}

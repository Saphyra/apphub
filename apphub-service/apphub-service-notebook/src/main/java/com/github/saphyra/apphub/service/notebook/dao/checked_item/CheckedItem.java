package com.github.saphyra.apphub.service.notebook.dao.checked_item;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder(toBuilder = true)
public class CheckedItem {
    @NonNull
    private final UUID checkedItemId;

    @NonNull
    private final UUID userId;

    @NonNull
    private Boolean checked;
}

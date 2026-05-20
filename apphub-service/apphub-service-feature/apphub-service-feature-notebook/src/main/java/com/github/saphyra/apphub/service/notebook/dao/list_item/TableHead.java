package com.github.saphyra.apphub.service.notebook.dao.list_item;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class TableHead {
    //TODO maybe unnecessary
    @NonNull
    private final UUID userId;
    //TODO maybe unnecessary
    @NonNull
    private final UUID listItemId;
    @NonNull
    private final UUID tableHeadId;
    private int index;
}

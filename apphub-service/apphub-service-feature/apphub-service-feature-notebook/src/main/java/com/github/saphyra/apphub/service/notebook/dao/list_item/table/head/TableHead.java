package com.github.saphyra.apphub.service.notebook.dao.list_item.table.head;

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
    @NonNull
    private final UUID tableHeadId;
    private int index;
}

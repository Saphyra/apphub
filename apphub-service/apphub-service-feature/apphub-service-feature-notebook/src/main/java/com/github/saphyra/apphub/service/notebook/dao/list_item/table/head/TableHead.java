package com.github.saphyra.apphub.service.notebook.dao.list_item.table.head;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
@NoArgsConstructor
public class TableHead {
    @NonNull
    private UUID tableHeadId;
    private int index;
}

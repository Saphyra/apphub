package com.github.saphyra.apphub.service.notebook.dao.column_type;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ColumnTypeFactory {
    public ColumnTypeDto create(UUID columnId, UUID userId, ColumnType columnType) {
        return ColumnTypeDto.builder()
            .columnId(columnId)
            .userId(userId)
            .type(columnType)
            .build();
    }
}

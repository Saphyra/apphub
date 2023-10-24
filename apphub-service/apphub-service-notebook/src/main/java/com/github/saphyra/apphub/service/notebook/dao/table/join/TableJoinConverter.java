package com.github.saphyra.apphub.service.notebook.dao.table.join;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_util.ForRemoval;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@ForRemoval("notebook-redesign")
public class TableJoinConverter extends ConverterBase<TableJoinEntity, TableJoin> {
    private final UuidConverter uuidConverter;

    @Override
    protected TableJoin processEntityConversion(TableJoinEntity entity) {
        return TableJoin.builder()
            .tableJoinId(uuidConverter.convertEntity(entity.getTableJoinId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .parent(uuidConverter.convertEntity(entity.getParent()))
            .rowIndex(entity.getRowIndex())
            .columnIndex(entity.getColumnIndex())
            .columnType(Optional.ofNullable(entity.getColumnType()).orElse(ColumnType.TEXT))
            .build();
    }

    @Override
    protected TableJoinEntity processDomainConversion(TableJoin domain) {
        return TableJoinEntity.builder()
            .tableJoinId(uuidConverter.convertDomain(domain.getTableJoinId()))
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .parent(uuidConverter.convertDomain(domain.getParent()))
            .rowIndex(domain.getRowIndex())
            .columnIndex(domain.getColumnIndex())
            .columnType(domain.getColumnType())
            .build();
    }
}

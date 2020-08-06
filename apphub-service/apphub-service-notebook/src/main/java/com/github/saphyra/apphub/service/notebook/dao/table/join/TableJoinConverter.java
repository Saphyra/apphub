package com.github.saphyra.apphub.service.notebook.dao.table.join;

import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import com.github.saphyra.converter.ConverterBase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
//TODO unit test
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
            .build();
    }
}

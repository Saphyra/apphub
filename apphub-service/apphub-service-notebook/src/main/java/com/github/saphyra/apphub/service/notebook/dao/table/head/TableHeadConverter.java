package com.github.saphyra.apphub.service.notebook.dao.table.head;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TableHeadConverter extends ConverterBase<TableHeadEntity, TableHead> {
    private final UuidConverter uuidConverter;

    @Override
    protected TableHead processEntityConversion(TableHeadEntity entity) {
        return TableHead.builder()
            .tableHeadId(uuidConverter.convertEntity(entity.getTableHeadId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .parent(uuidConverter.convertEntity(entity.getParent()))
            .columnIndex(entity.getColumnIndex())
            .build();
    }

    @Override
    protected TableHeadEntity processDomainConversion(TableHead domain) {
        return TableHeadEntity.builder()
            .tableHeadId(uuidConverter.convertDomain(domain.getTableHeadId()))
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .parent(uuidConverter.convertDomain(domain.getParent()))
            .columnIndex(domain.getColumnIndex())
            .build();
    }
}

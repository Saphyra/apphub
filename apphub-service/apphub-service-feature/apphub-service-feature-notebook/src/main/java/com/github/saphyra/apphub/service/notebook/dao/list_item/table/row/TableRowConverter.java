package com.github.saphyra.apphub.service.notebook.dao.list_item.table.row;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.BooleanEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.IntegerEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
class TableRowConverter extends ConverterBase<TableRowEntity, TableRow> {
    static final String COLUMN_INDEX = "index";
    static final String COLUMN_CHECKED = "checked";
    static final String COLUMN_COLUMNS = "columns";

    private final IntegerEncryptor integerEncryptor;
    private final BooleanEncryptor booleanEncryptor;
    private final StringEncryptor stringEncryptor;
    private final UuidConverter uuidConverter;
    private final AccessTokenProvider accessTokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    protected TableRowEntity processDomainConversion(TableRow domain) {
        String tableRowId = uuidConverter.convertDomain(domain.getTableRowId());
        String userId = accessTokenProvider.getUserIdAsString();

        return TableRowEntity.builder()
            .tableRowId(tableRowId)
            .listItemId(uuidConverter.convertDomain(domain.getListItemId()))
            .index(integerEncryptor.encrypt(domain.getIndex(), userId, tableRowId, COLUMN_INDEX))
            .checked(booleanEncryptor.encrypt(domain.getChecked(), userId, tableRowId, COLUMN_CHECKED))
            .columns(stringEncryptor.encrypt(objectMapper.writeValueAsString(domain.getColumns()), userId, tableRowId, COLUMN_COLUMNS))
            .build();
    }

    @Override
    protected TableRow processEntityConversion(TableRowEntity entity) {
        String tableRowId = entity.getTableRowId();
        String userId = accessTokenProvider.getUserIdAsString();

        String decryptedColumns = stringEncryptor.decrypt(entity.getColumns(), userId, tableRowId, COLUMN_COLUMNS);
        List<TableColumn> columns = objectMapper.readValue(decryptedColumns, new TypeReference<>() {
        });

        return TableRow.builder()
            .tableRowId(uuidConverter.convertEntity(tableRowId))
            .listItemId(uuidConverter.convertEntity(entity.getListItemId()))
            .index(integerEncryptor.decrypt(entity.getIndex(), userId, tableRowId, COLUMN_INDEX))
            .checked(Optional.ofNullable(booleanEncryptor.decrypt(entity.getChecked(), userId, tableRowId, COLUMN_CHECKED)).orElse(false))
            .columns(columns)
            .build();
    }
}

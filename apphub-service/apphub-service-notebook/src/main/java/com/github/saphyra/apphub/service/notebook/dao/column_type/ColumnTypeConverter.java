package com.github.saphyra.apphub.service.notebook.dao.column_type;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class ColumnTypeConverter extends ConverterBase<ColumnTypeEntity, ColumnTypeDto> {
    private final AccessTokenProvider accessTokenProvider;
    private final UuidConverter uuidConverter;
    private final StringEncryptor stringEncryptor;

    @Override
    protected ColumnTypeEntity processDomainConversion(ColumnTypeDto domain) {
        return ColumnTypeEntity.builder()
            .columnId(uuidConverter.convertDomain(domain.getColumnId()))
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .type(stringEncryptor.encryptEntity(domain.getType().name(), accessTokenProvider.getUserIdAsString()))
            .build();
    }

    @Override
    protected ColumnTypeDto processEntityConversion(ColumnTypeEntity entity) {
        return ColumnTypeDto.builder()
            .columnId(uuidConverter.convertEntity(entity.getColumnId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .type(ColumnType.valueOf(stringEncryptor.decryptEntity(entity.getType(), accessTokenProvider.getUserIdAsString())))
            .build();
    }
}

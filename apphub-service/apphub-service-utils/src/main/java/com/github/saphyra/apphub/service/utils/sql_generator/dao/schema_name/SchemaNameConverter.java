package com.github.saphyra.apphub.service.utils.sql_generator.dao.schema_name;

import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption_dao.EncryptionConverter;
import com.github.saphyra.apphub.lib.encryption_dao.EncryptionService;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class SchemaNameConverter extends EncryptionConverter<SchemaNameEntity, SchemaName> {
    private static final String COLUMN_SCHEMA_NAME = "schema_name";

    private final EncryptionService encryptionService;
    private final UuidConverter uuidConverter;
    private final AccessTokenProvider accessTokenProvider;

    @Override
    protected SchemaNameEntity processDomainConversion(SchemaName domain) {
        return SchemaNameEntity.builder()
            .schemaNameId(uuidConverter.convertDomain(domain.getSchemaNameId()))
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .externalReference(uuidConverter.convertDomain(domain.getExternalReference()))
            .schemaName(encryptionService.encrypt(
                domain.getSchemaName(),
                COLUMN_SCHEMA_NAME,
                domain.getSchemaNameId(),
                DataType.UTILS_SQL_GENERATOR_SCHEMA_NAME,
                encryptionKey -> accessTokenProvider.get().getUserId().equals(domain.getUserId())
            ))
            .build();
    }

    @Override
    protected SchemaName processEntityConversion(SchemaNameEntity entity) {
        UUID schemaNameId = uuidConverter.convertEntity(entity.getSchemaNameId());
        UUID userId = uuidConverter.convertEntity(entity.getUserId());
        return SchemaName.builder()
            .schemaNameId(schemaNameId)
            .userId(userId)
            .externalReference(uuidConverter.convertEntity(entity.getExternalReference()))
            .schemaName(encryptionService.decryptString(
                entity.getSchemaName(),
                COLUMN_SCHEMA_NAME,
                schemaNameId,
                DataType.UTILS_SQL_GENERATOR_SCHEMA_NAME,
                encryptionKey -> accessTokenProvider.get().getUserId().equals(userId)
            ))
            .build();
    }
}

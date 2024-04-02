package com.github.saphyra.apphub.service.utils.sql_generator.dao.segment_value;

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
class SegmentValueConverter extends EncryptionConverter<SegmentValueEntity, SegmentValue> {
    private static final String COLUMN_SEG_VALUE = "segment_value";

    private final EncryptionService encryptionService;
    private final UuidConverter uuidConverter;
    private final AccessTokenProvider accessTokenProvider;

    @Override
    protected SegmentValueEntity processDomainConversion(SegmentValue domain) {
        return SegmentValueEntity.builder()
            .segmentValueId(uuidConverter.convertDomain(domain.getSegmentValueId()))
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .externalReference(uuidConverter.convertDomain(domain.getExternalReference()))
            .segmentValue(encryptionService.encrypt(
                domain.getSegmentValue(),
                COLUMN_SEG_VALUE,
                domain.getSegmentValueId(),
                DataType.UTILS_SQL_GENERATOR_SEGMENT_VALUE,
                encryptionKey -> accessTokenProvider.get().getUserId().equals(domain.getUserId())
            ))
            .build();
    }

    @Override
    protected SegmentValue processEntityConversion(SegmentValueEntity entity) {
        UUID schemaNameId = uuidConverter.convertEntity(entity.getSegmentValueId());
        UUID userId = uuidConverter.convertEntity(entity.getUserId());
        return SegmentValue.builder()
            .segmentValueId(schemaNameId)
            .userId(userId)
            .externalReference(uuidConverter.convertEntity(entity.getExternalReference()))
            .segmentValue(encryptionService.decryptString(
                entity.getSegmentValue(),
                COLUMN_SEG_VALUE,
                schemaNameId,
                DataType.UTILS_SQL_GENERATOR_SEGMENT_VALUE,
                encryptionKey -> accessTokenProvider.get().getUserId().equals(userId)
            ))
            .build();
    }
}

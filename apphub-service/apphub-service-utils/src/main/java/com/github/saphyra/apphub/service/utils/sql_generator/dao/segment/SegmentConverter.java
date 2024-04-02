package com.github.saphyra.apphub.service.utils.sql_generator.dao.segment;

import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.utils.model.sql_generator.SegmentType;
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
class SegmentConverter extends EncryptionConverter<SegmentEntity, Segment> {
    private static final String COLUMN_SEGMENT_TYPE = "segment_type";
    private static final String COLUMN_ORDER = "order";

    private final EncryptionService encryptionService;
    private final UuidConverter uuidConverter;
    private final AccessTokenProvider accessTokenProvider;

    @Override
    protected SegmentEntity processDomainConversion(Segment domain) {
        return SegmentEntity.builder()
            .segmentId(uuidConverter.convertDomain(domain.getSegmentId()))
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .externalReference(uuidConverter.convertDomain(domain.getExternalReference()))
            .segmentType(encryptionService.encrypt(
                domain.getSegmentType().name(),
                COLUMN_SEGMENT_TYPE,
                domain.getSegmentId(),
                DataType.UTILS_SQL_GENERATOR_SEGMENT,
                encryptionKey -> accessTokenProvider.get().getUserId().equals(domain.getUserId())
            ))
            .order(encryptionService.encrypt(
                domain.getOrder(),
                COLUMN_ORDER,
                domain.getSegmentId(),
                DataType.UTILS_SQL_GENERATOR_SEGMENT,
                encryptionKey -> accessTokenProvider.get().getUserId().equals(domain.getUserId())
            ))
            .build();
    }

    @Override
    protected Segment processEntityConversion(SegmentEntity entity) {
        UUID segmentId = uuidConverter.convertEntity(entity.getSegmentId());
        UUID userId = uuidConverter.convertEntity(entity.getUserId());
        return Segment.builder()
            .segmentId(segmentId)
            .userId(userId)
            .externalReference(uuidConverter.convertEntity(entity.getExternalReference()))
            .segmentType(SegmentType.valueOf(decryptSegmentType(entity.getSegmentType(), segmentId, userId)))
            .order(encryptionService.decryptInteger(
                entity.getOrder(),
                COLUMN_ORDER,
                segmentId,
                DataType.UTILS_SQL_GENERATOR_SEGMENT,
                encryptionKey -> accessTokenProvider.get().getUserId().equals(userId)
            ))
            .build();
    }

    private String decryptSegmentType(String segmentType, UUID segmentId, UUID userId) {
        return encryptionService.decryptString(
            segmentType,
            COLUMN_SEGMENT_TYPE,
            segmentId,
            DataType.UTILS_SQL_GENERATOR_SEGMENT,
            encryptionKey -> accessTokenProvider.get().getUserId().equals(userId)
        );
    }
}

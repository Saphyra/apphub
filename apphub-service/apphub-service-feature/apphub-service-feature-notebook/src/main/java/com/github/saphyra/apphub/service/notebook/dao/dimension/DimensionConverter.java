package com.github.saphyra.apphub.service.notebook.dao.dimension;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.IntegerEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class DimensionConverter extends ConverterBase<DimensionEntity, Dimension> {
    static final String COLUMN_INDEX = "index";

    private final AccessTokenProvider accessTokenProvider;
    private final UuidConverter uuidConverter;
    private final IntegerEncryptor integerEncryptor;

    @Override
    protected DimensionEntity processDomainConversion(Dimension domain) {
        String dimensionId = uuidConverter.convertDomain(domain.getDimensionId());
        return DimensionEntity.builder()
            .dimensionId(dimensionId)
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .externalReference(uuidConverter.convertDomain(domain.getExternalReference()))
            .index(integerEncryptor.encrypt(domain.getIndex(), accessTokenProvider.getUserIdAsString(), dimensionId, COLUMN_INDEX))
            .build();
    }

    @Override
    protected Dimension processEntityConversion(DimensionEntity entity) {
        return Dimension.builder()
            .dimensionId(uuidConverter.convertEntity(entity.getDimensionId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .externalReference(uuidConverter.convertEntity(entity.getExternalReference()))
            .index(integerEncryptor.decrypt(entity.getIndex(), accessTokenProvider.getUserIdAsString(), entity.getDimensionId(), COLUMN_INDEX))
            .build();
    }
}

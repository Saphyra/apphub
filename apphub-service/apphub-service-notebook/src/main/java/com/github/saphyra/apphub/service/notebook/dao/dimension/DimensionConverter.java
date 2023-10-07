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
//TODO unit test
class DimensionConverter extends ConverterBase<DimensionEntity, Dimension> {
    private final AccessTokenProvider accessTokenProvider;
    private final UuidConverter uuidConverter;
    private final IntegerEncryptor integerEncryptor;

    @Override
    protected DimensionEntity processDomainConversion(Dimension domain) {
        return DimensionEntity.builder()
            .dimensionId(uuidConverter.convertDomain(domain.getDimensionId()))
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .externalReference(uuidConverter.convertDomain(domain.getExternalReference()))
            .index(integerEncryptor.encryptEntity(domain.getIndex(), accessTokenProvider.getUserIdAsString()))
            .build();
    }

    @Override
    protected Dimension processEntityConversion(DimensionEntity entity) {
        return Dimension.builder()
            .dimensionId(uuidConverter.convertEntity(entity.getDimensionId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .externalReference(uuidConverter.convertEntity(entity.getExternalReference()))
            .index(integerEncryptor.decryptEntity(entity.getIndex(), accessTokenProvider.getUserIdAsString()))
            .build();
    }
}

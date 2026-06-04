package com.github.saphyra.apphub.service.feature.calendar.domain.label.deprecated_dao;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@Deprecated(forRemoval = true)
class DeprecatedLabelConverter extends ConverterBase<DeprecatedLabelEntity, DeprecatedLabel> {
    static final String COLUMN_LABEL = "label";

    private final AccessTokenProvider accessTokenProvider;
    private final UuidConverter uuidConverter;
    private final StringEncryptor stringEncryptor;

    @Override
    protected DeprecatedLabelEntity processDomainConversion(DeprecatedLabel domain) {
        String labelId = uuidConverter.convertDomain(domain.getLabelId());
        return DeprecatedLabelEntity.builder()
            .labelId(labelId)
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .label(stringEncryptor.encrypt(domain.getLabel(), accessTokenProvider.getUserIdAsString(), labelId, COLUMN_LABEL))
            .build();
    }

    @Override
    protected DeprecatedLabel processEntityConversion(DeprecatedLabelEntity entity) {
        return DeprecatedLabel.builder()
            .labelId(uuidConverter.convertEntity(entity.getLabelId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .label(stringEncryptor.decrypt(entity.getLabel(), accessTokenProvider.getUserIdAsString(), entity.getLabelId(), COLUMN_LABEL))
            .build();
    }
}

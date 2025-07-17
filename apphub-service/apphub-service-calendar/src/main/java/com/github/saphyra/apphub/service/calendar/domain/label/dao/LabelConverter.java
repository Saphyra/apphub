package com.github.saphyra.apphub.service.calendar.domain.label.dao;

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
//TODO unit test
class LabelConverter extends ConverterBase<LabelEntity, Label> {
    private static final String COLUMN_LABEL = "label";

    private final AccessTokenProvider accessTokenProvider;
    private final UuidConverter uuidConverter;
    private final StringEncryptor stringEncryptor;

    @Override
    protected LabelEntity processDomainConversion(Label domain) {
        String labelId = uuidConverter.convertDomain(domain.getLabelId());
        return LabelEntity.builder()
            .labelId(labelId)
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .label(stringEncryptor.encrypt(domain.getLabel(), accessTokenProvider.getUserIdAsString(), labelId, COLUMN_LABEL))
            .build();
    }

    @Override
    protected Label processEntityConversion(LabelEntity entity) {
        return Label.builder()
            .labelId(uuidConverter.convertEntity(entity.getLabelId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .label(stringEncryptor.decrypt(entity.getLabel(), accessTokenProvider.getUserIdAsString(), entity.getLabelId(), COLUMN_LABEL))
            .build();
    }
}

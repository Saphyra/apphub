package com.github.saphyra.apphub.service.feature.calendar.domain.label.dao;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_LABEL;

@Component
@RequiredArgsConstructor
//TODO unit test
class LabelConverter extends ConverterBase<LabelEntity, Label> {
    private final UuidConverter uuidConverter;
    private final StringEncryptor stringEncryptor;
    private final AccessTokenProvider accessTokenProvider;

    @Override
    protected LabelEntity processDomainConversion(Label domain) {
        String labelId = uuidConverter.convertDomain(domain.getLabelId());
        return LabelEntity.builder()
            .labelId(labelId)
            .label(stringEncryptor.encrypt(domain.getLabel(), accessTokenProvider.getUserIdAsString(), labelId, COLUMN_LABEL))
            .build();
    }

    @Override
    protected Label processEntityConversion(LabelEntity entity) {
        return Label.builder()
            .labelId(uuidConverter.convertEntity(entity.getLabelId()))
            .label(stringEncryptor.decrypt(entity.getLabel(), accessTokenProvider.getUserIdAsString(), entity.getLabelId(), COLUMN_LABEL))
            .build();
    }
}

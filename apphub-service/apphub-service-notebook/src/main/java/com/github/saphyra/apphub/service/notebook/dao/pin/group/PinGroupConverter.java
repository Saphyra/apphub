package com.github.saphyra.apphub.service.notebook.dao.pin.group;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
class PinGroupConverter extends ConverterBase<PinGroupEntity, PinGroup> {
    static final String COLUMN_PIN_GROUP_NAME = "pin-group-name";
    static final String COLUMN_LAST_OPENED = "last-opened";

    private final UuidConverter uuidConverter;
    private final StringEncryptor stringEncryptor;
    private final AccessTokenProvider accessTokenProvider;

    @Override
    protected PinGroupEntity processDomainConversion(PinGroup domain) {
        String pinGroupId = uuidConverter.convertDomain(domain.getPinGroupId());

        return PinGroupEntity.builder()
            .pinGroupId(pinGroupId)
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .pinGroupName(stringEncryptor.encrypt(domain.getPinGroupName(), accessTokenProvider.getUserIdAsString(), pinGroupId, COLUMN_PIN_GROUP_NAME))
            .lastOpened(Optional.ofNullable(domain.getLastOpened())
                .map(lastUpdated -> stringEncryptor.encrypt(
                    lastUpdated.toString(),
                    accessTokenProvider.getUserIdAsString(),
                    pinGroupId,
                    COLUMN_LAST_OPENED
                ))
                .orElse(null))
            .build();
    }

    @Override
    protected PinGroup processEntityConversion(PinGroupEntity entity) {
        return PinGroup.builder()
            .pinGroupId(uuidConverter.convertEntity(entity.getPinGroupId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .pinGroupName(stringEncryptor.decrypt(entity.getPinGroupName(), accessTokenProvider.getUserIdAsString(), entity.getPinGroupId(), COLUMN_PIN_GROUP_NAME))
            .lastOpened(Optional.ofNullable(entity.getLastOpened())
                .map(lastUpdated -> stringEncryptor.decrypt(
                    lastUpdated,
                    accessTokenProvider.getUserIdAsString(),
                    entity.getPinGroupId(),
                    COLUMN_LAST_OPENED)
                )
                .map(LocalDateTime::parse)
                .orElse(LocalDateTime.MIN))
            .build();
    }
}

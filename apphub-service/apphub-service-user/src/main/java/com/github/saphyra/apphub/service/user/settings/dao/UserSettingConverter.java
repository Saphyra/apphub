package com.github.saphyra.apphub.service.user.settings.dao;

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
class UserSettingConverter extends ConverterBase<UserSettingEntity, UserSetting> {
    static final String COLUMN_VALUE = "value";

    private final UuidConverter uuidConverter;
    private final StringEncryptor stringEncryptor;
    private final AccessTokenProvider accessTokenProvider;

    @Override
    protected UserSettingEntity processDomainConversion(UserSetting domain) {
        String userId = accessTokenProvider.getUserIdAsString();
        return UserSettingEntity.builder()
            .id(
                UserSettingEntityId.builder()
                    .userId(uuidConverter.convertDomain(domain.getUserId()))
                    .category(domain.getCategory())
                    .key(domain.getKey())
                    .build()
            )
            .value(stringEncryptor.encrypt(domain.getValue(), userId, domain.getKey(), COLUMN_VALUE))
            .build();
    }

    @Override
    protected UserSetting processEntityConversion(UserSettingEntity entity) {
        String userId = accessTokenProvider.getUserIdAsString();
        return UserSetting.builder()
            .userId(uuidConverter.convertEntity(entity.getId().getUserId()))
            .category(entity.getId().getCategory())
            .key(entity.getId().getKey())
            .value(stringEncryptor.decrypt(entity.getValue(), userId, entity.getId().getKey(), COLUMN_VALUE))
            .build();
    }
}

package com.github.saphyra.apphub.service.notebook.dao.pin_group;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.HashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
class PinGroupConverter extends ConverterBase<PinGroupEntity, PinGroup> {
    private final UuidConverter uuidConverter;
    private final AccessTokenProvider accessTokenProvider;
    private final StringEncryptor stringEncryptor;
    private final ObjectMapper objectMapper;
    private final DateTimeUtil dateTimeUtil;

    @Override
    protected PinGroupEntity processDomainConversion(PinGroup domain) {
        String accessTokenUserId = accessTokenProvider.getUserIdAsString();
        String pinGroupId = uuidConverter.convertDomain(domain.getPinGroupId());

        return PinGroupEntity.builder()
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .pinGroupId(pinGroupId)
            .pinGroupName(stringEncryptor.encrypt(domain.getPinGroupName(), accessTokenUserId, pinGroupId, PinGroupDaoConstants.COLUMN_PIN_GROUP_NAME))
            .lastOpened(dateTimeUtil.toEpochSecond(domain.getLastOpened()))
            .listItemIds(objectMapper.writeValueAsString(domain.getListItemIds()))
            .build();
    }

    @Override
    protected PinGroup processEntityConversion(PinGroupEntity entity) {
        TypeReference<List<String>> typeReference = new TypeReference<>() {
        };

        return PinGroup.builder()
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .pinGroupId(uuidConverter.convertEntity(entity.getPinGroupId()))
            .pinGroupName(stringEncryptor.decrypt(entity.getPinGroupName(), accessTokenProvider.getUserIdAsString(), entity.getPinGroupId(), PinGroupDaoConstants.COLUMN_PIN_GROUP_NAME))
            .lastOpened(dateTimeUtil.fromEpochSecond(entity.getLastOpened()))
            .listItemIds(new HashSet<>(uuidConverter.convertEntity(objectMapper.readValue(entity.getListItemIds(), typeReference))))
            .build();
    }
}

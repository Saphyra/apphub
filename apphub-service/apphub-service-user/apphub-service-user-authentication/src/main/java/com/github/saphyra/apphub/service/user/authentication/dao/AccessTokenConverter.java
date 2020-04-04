package com.github.saphyra.apphub.service.user.authentication.dao;

import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import com.github.saphyra.converter.ConverterBase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccessTokenConverter extends ConverterBase<AccessTokenEntity, AccessToken> {
    private final UuidConverter uuidConverter;

    @Override
    protected AccessToken processEntityConversion(AccessTokenEntity entity) {
        return AccessToken.builder()
            .accessTokenId(uuidConverter.convertEntity(entity.getAccessTokenId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .persistent(entity.isPersistent())
            .lastAccess(entity.getLastAccess())
            .build();
    }

    @Override
    protected AccessTokenEntity processDomainConversion(AccessToken domain) {
        return AccessTokenEntity.builder()
            .accessTokenId(uuidConverter.convertDomain(domain.getAccessTokenId()))
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .persistent(domain.isPersistent())
            .lastAccess(domain.getLastAccess())
            .build();
    }
}

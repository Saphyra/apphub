package com.github.saphrya.apphub.service.platform.authorization.dao.refresh_token;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class RefreshTokenConverter extends ConverterBase<RefreshTokenEntity, RefreshToken> {
    private final UuidConverter uuidConverter;
    private final DateTimeUtil dateTimeUtil;

    @Override
    protected RefreshTokenEntity processDomainConversion(RefreshToken domain) {
        return RefreshTokenEntity.builder()
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .refreshTokenId(uuidConverter.convertDomain(domain.getRefreshTokenId()))
            .issuedAt(dateTimeUtil.toEpochSecond(domain.getIssuedAt()))
            .expiration(dateTimeUtil.toEpochSecond(domain.getExpiration()))
            .rememberMe(domain.isRememberMe())
            .build();
    }

    @Override
    protected RefreshToken processEntityConversion(RefreshTokenEntity entity) {
        return RefreshToken.builder()
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .refreshTokenId(uuidConverter.convertEntity(entity.getRefreshTokenId()))
            .issuedAt(dateTimeUtil.fromEpochSecond(entity.getIssuedAt()))
            .expiration(dateTimeUtil.fromEpochSecond(entity.getExpiration()))
            .rememberMe(entity.getRememberMe())
            .build();
    }
}

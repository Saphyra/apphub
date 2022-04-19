package com.github.saphyra.apphub.service.community.blacklist.dao;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class BlacklistConverter extends ConverterBase<BlacklistEntity, Blacklist> {
    private final UuidConverter uuidConverter;

    @Override
    protected Blacklist processEntityConversion(BlacklistEntity entity) {
        return Blacklist.builder()
            .blacklistId(uuidConverter.convertEntity(entity.getBlacklistId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .blockedUserId(uuidConverter.convertEntity(entity.getBlockedUserId()))
            .build();
    }

    @Override
    protected BlacklistEntity processDomainConversion(Blacklist domain) {
        return BlacklistEntity.builder()
            .blacklistId(uuidConverter.convertDomain(domain.getBlacklistId()))
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .blockedUserId(uuidConverter.convertDomain(domain.getBlockedUserId()))
            .build();
    }
}

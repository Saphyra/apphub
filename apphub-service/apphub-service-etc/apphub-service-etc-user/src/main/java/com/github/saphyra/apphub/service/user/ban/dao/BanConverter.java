package com.github.saphyra.apphub.service.user.ban.dao;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class BanConverter extends ConverterBase<BanEntity, Ban> {
    private final UuidConverter uuidConverter;

    @Override
    protected Ban processEntityConversion(BanEntity entity) {
        return Ban.builder()
            .id(uuidConverter.convertEntity(entity.getId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .bannedRole(entity.getBannedRole())
            .expiration(entity.getExpiration())
            .permanent(entity.getPermanent())
            .reason(entity.getReason())
            .bannedBy(uuidConverter.convertEntity(entity.getBannedBy()))
            .build();
    }

    @Override
    protected BanEntity processDomainConversion(Ban domain) {
        return BanEntity.builder()
            .id(uuidConverter.convertDomain(domain.getId()))
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .bannedRole(domain.getBannedRole())
            .expiration(domain.getExpiration())
            .permanent(domain.getPermanent())
            .reason(domain.getReason())
            .bannedBy(uuidConverter.convertDomain(domain.getBannedBy()))
            .build();
    }
}

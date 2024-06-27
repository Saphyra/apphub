package com.github.saphyra.apphub.service.skyxplore.data.setting.dao;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class SettingConverter extends ConverterBase<SettingEntity, Setting> {
    private final UuidConverter uuidConverter;

    @Override
    protected SettingEntity processDomainConversion(Setting domain) {
        return SettingEntity.builder()
            .settingId(uuidConverter.convertDomain(domain.getSettingId()))
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .type(domain.getType())
            .location(uuidConverter.convertDomain(domain.getLocation()))
            .data(domain.getData())
            .build();
    }

    @Override
    protected Setting processEntityConversion(SettingEntity entity) {
        return Setting.builder()
            .settingId(uuidConverter.convertEntity(entity.getSettingId()))
            .gameId(uuidConverter.convertEntity(entity.getGameId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .type(entity.getType())
            .location(uuidConverter.convertEntity(entity.getLocation()))
            .data(entity.getData())
            .build();
    }
}

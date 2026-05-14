package com.github.saphyra.apphub.service.user.data.dao.role_deprecated;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Deprecated(forRemoval = true)
class RoleConverter extends ConverterBase<RoleEntity, RoleDto> {
    private final UuidConverter uuidConverter;

    @Override
    protected RoleDto processEntityConversion(RoleEntity entity) {
        return RoleDto.builder()
            .roleId(uuidConverter.convertEntity(entity.getRoleId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .role(entity.getRole())
            .build();
    }

    @Override
    protected RoleEntity processDomainConversion(RoleDto domain) {
        return RoleEntity.builder()
            .roleId(uuidConverter.convertDomain(domain.getRoleId()))
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .role(domain.getRole())
            .build();
    }
}

package com.github.saphyra.apphub.service.user.data.dao.role;

import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import com.github.saphyra.converter.ConverterBase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class RoleConverter extends ConverterBase<RoleEntity, Role> {
    private final UuidConverter uuidConverter;

    @Override
    protected Role processEntityConversion(RoleEntity entity) {
        return Role.builder()
            .roleId(uuidConverter.convertEntity(entity.getRoleId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .role(entity.getRole())
            .build();
    }

    @Override
    protected RoleEntity processDomainConversion(Role domain) {
        return RoleEntity.builder()
            .roleId(uuidConverter.convertDomain(domain.getRoleId()))
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .role(domain.getRole())
            .build();
    }
}

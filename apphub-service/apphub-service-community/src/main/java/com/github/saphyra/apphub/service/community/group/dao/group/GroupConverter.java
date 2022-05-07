package com.github.saphyra.apphub.service.community.group.dao.group;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class GroupConverter extends ConverterBase<GroupEntity, Group> {
    private final UuidConverter uuidConverter;

    @Override
    protected GroupEntity processDomainConversion(Group domain) {
        return GroupEntity.builder()
            .groupId(uuidConverter.convertDomain(domain.getGroupId()))
            .ownerId(uuidConverter.convertDomain(domain.getOwnerId()))
            .name(domain.getName())
            .invitationType(domain.getInvitationType())
            .build();
    }

    @Override
    protected Group processEntityConversion(GroupEntity entity) {
        return Group.builder()
            .groupId(uuidConverter.convertEntity(entity.getGroupId()))
            .ownerId(uuidConverter.convertEntity(entity.getOwnerId()))
            .name(entity.getName())
            .invitationType(entity.getInvitationType())
            .build();
    }
}

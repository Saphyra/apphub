package com.github.saphyra.apphub.service.community.group.dao.member;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Component
@RequiredArgsConstructor
class GroupMemberConverter extends ConverterBase<GroupMemberEntity, GroupMember> {
    private final UuidConverter uuidConverter;

    @Override
    protected GroupMemberEntity processDomainConversion(GroupMember domain) {
        return GroupMemberEntity.builder()
            .groupMemberId(uuidConverter.convertDomain(domain.getGroupMemberId()))
            .groupId(uuidConverter.convertDomain(domain.getGroupId()))
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .canInvite(domain.isCanInvite())
            .canKick(domain.isCanKick())
            .canModifyRoles(domain.isCanModifyRoles())
            .build();
    }

    @Override
    protected GroupMember processEntityConversion(GroupMemberEntity entity) {
        return GroupMember.builder()
            .groupMemberId(uuidConverter.convertEntity(entity.getGroupMemberId()))
            .groupId(uuidConverter.convertEntity(entity.getGroupId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .canInvite(isTrue(entity.getCanInvite()))
            .canKick(isTrue(entity.getCanKick()))
            .canModifyRoles(isTrue(entity.getCanModifyRoles()))
            .build();
    }
}

package com.github.saphyra.apphub.service.community.group.service;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.community.group.dao.member.GroupMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class GroupMemberFactory {
    private final IdGenerator idGenerator;

    GroupMember create(UUID groupId, UUID userId, boolean hasRoles) {
        return GroupMember.builder()
            .groupMemberId(idGenerator.randomUuid())
            .groupId(groupId)
            .userId(userId)
            .canInvite(hasRoles)
            .canKick(hasRoles)
            .canModifyRoles(hasRoles)
            .build();
    }
}

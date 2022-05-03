package com.github.saphyra.apphub.service.community.group.service.group_member;

import com.github.saphyra.apphub.api.community.model.response.group.GroupMemberResponse;
import com.github.saphyra.apphub.api.user.model.response.AccountResponse;
import com.github.saphyra.apphub.service.community.common.AccountClientProxy;
import com.github.saphyra.apphub.service.community.group.dao.member.GroupMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class GroupMemberToResponseConverter {
    private final AccountClientProxy accountClientProxy;

    public GroupMemberResponse convert(GroupMember groupMember) {
        AccountResponse accountResponse = accountClientProxy.getAccount(groupMember.getUserId());

        return GroupMemberResponse.builder()
            .groupMemberId(groupMember.getGroupMemberId())
            .userId(groupMember.getUserId())
            .username(accountResponse.getUsername())
            .email(accountResponse.getEmail())
            .canModifyRoles(groupMember.isCanModifyRoles())
            .canInvite(groupMember.isCanInvite())
            .canKick(groupMember.isCanKick())
            .build();
    }
}

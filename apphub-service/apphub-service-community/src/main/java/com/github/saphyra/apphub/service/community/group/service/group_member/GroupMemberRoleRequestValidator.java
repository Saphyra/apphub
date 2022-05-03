package com.github.saphyra.apphub.service.community.group.service.group_member;

import com.github.saphyra.apphub.api.community.model.response.group.GroupMemberRoleRequest;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class GroupMemberRoleRequestValidator {
    void validate(GroupMemberRoleRequest request) {
        ValidationUtil.notNull(request.getCanInvite(), "canInvite");
        ValidationUtil.notNull(request.getCanKick(), "canKick");
        ValidationUtil.notNull(request.getCanModifyRoles(), "canModifyRoles");
    }
}

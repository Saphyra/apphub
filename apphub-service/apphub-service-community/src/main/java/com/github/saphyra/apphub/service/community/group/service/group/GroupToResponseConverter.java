package com.github.saphyra.apphub.service.community.group.service.group;

import com.github.saphyra.apphub.api.community.model.response.group.GroupListResponse;
import com.github.saphyra.apphub.service.community.group.dao.group.Group;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class GroupToResponseConverter {
    GroupListResponse convert(Group group) {
        return GroupListResponse.builder()
            .groupId(group.getGroupId())
            .name(group.getName())
            .ownerId(group.getOwnerId())
            .invitationType(group.getInvitationType())
            .build();
    }
}

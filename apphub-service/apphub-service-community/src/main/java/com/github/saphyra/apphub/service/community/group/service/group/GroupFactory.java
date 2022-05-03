package com.github.saphyra.apphub.service.community.group.service.group;

import com.github.saphyra.apphub.api.community.model.response.group.GroupInvitationType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.community.group.dao.group.Group;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class GroupFactory {
    private final IdGenerator idGenerator;

    Group create(UUID userId, String groupName) {
        return Group.builder()
            .groupId(idGenerator.randomUuid())
            .ownerId(userId)
            .name(groupName)
            .invitationType(GroupInvitationType.FRIENDS)
            .build();
    }
}

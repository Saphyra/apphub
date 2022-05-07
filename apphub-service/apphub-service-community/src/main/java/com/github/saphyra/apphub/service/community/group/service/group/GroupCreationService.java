package com.github.saphyra.apphub.service.community.group.service.group;

import com.github.saphyra.apphub.api.community.model.response.group.GroupListResponse;
import com.github.saphyra.apphub.service.community.group.dao.group.Group;
import com.github.saphyra.apphub.service.community.group.dao.group.GroupDao;
import com.github.saphyra.apphub.service.community.group.dao.member.GroupMember;
import com.github.saphyra.apphub.service.community.group.dao.member.GroupMemberDao;
import com.github.saphyra.apphub.service.community.group.service.group_member.GroupMemberFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class GroupCreationService {
    private final GroupNameValidator groupNameValidator;
    private final GroupFactory groupFactory;
    private final GroupMemberFactory groupMemberFactory;
    private final GroupDao groupDao;
    private final GroupMemberDao groupMemberDao;
    private final GroupToResponseConverter groupToResponseConverter;

    public GroupListResponse create(UUID userId, String groupName) {
        groupNameValidator.validate(groupName);

        Group group = groupFactory.create(userId, groupName);
        GroupMember member = groupMemberFactory.create(group.getGroupId(), userId, true);

        groupDao.save(group);
        groupMemberDao.save(member);

        return groupToResponseConverter.convert(group);
    }
}

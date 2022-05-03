package com.github.saphyra.apphub.service.community.group.service.group_member;

import com.github.saphyra.apphub.api.community.model.response.group.GroupMemberResponse;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.community.group.dao.member.GroupMember;
import com.github.saphyra.apphub.service.community.group.dao.member.GroupMemberDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class GroupMemberQueryService {
    private final GroupMemberDao groupMemberDao;
    private final GroupMemberToResponseConverter groupMemberToResponseConverter;

    public List<GroupMemberResponse> getMembers(UUID userId, UUID groupId) {
        List<GroupMember> members = groupMemberDao.getByGroupId(groupId);

        if (members.stream().noneMatch(groupMember -> groupMember.getUserId().equals(userId))) {
            throw ExceptionFactory.forbiddenOperation(userId + " must not query GroupMembers of groupId " + groupId);
        }

        return members.stream()
            .map(groupMemberToResponseConverter::convert)
            .collect(Collectors.toList());
    }
}

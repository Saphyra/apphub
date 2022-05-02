package com.github.saphyra.apphub.service.community.group.service;

import com.github.saphyra.apphub.api.community.model.response.group.GroupListResponse;
import com.github.saphyra.apphub.service.community.group.dao.group.GroupDao;
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
public class GroupQueryService {
    private final GroupMemberDao groupMemberDao;
    private final GroupDao groupDao;
    private final GroupToResponseConverter groupToResponseConverter;

    public List<GroupListResponse> getGroups(UUID userId) {
        return groupMemberDao.getByUserId(userId)
            .stream()
            .map(groupMember -> groupDao.findByIdValidated(groupMember.getGroupId()))
            .map(groupToResponseConverter::convert)
            .collect(Collectors.toList());
    }
}

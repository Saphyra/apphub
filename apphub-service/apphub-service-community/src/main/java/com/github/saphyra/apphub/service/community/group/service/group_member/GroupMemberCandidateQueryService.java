package com.github.saphyra.apphub.service.community.group.service.group_member;

import com.github.saphyra.apphub.api.community.model.response.SearchResultItem;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.community.common.AccountClientProxy;
import com.github.saphyra.apphub.service.community.common.AccountResponseToSearchResultItemConverter;
import com.github.saphyra.apphub.service.community.group.dao.group.Group;
import com.github.saphyra.apphub.service.community.group.dao.group.GroupDao;
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
public class GroupMemberCandidateQueryService {
    private final GroupDao groupDao;
    private final GroupMemberDao groupMemberDao;
    private final GroupMemberCandidateCollector groupMemberCandidateCollector;
    private final AccountClientProxy accountClientProxy;
    private final AccountResponseToSearchResultItemConverter accountResponseToSearchResultItemConverter;

    public List<SearchResultItem> search(UUID userId, UUID groupId, String query) {
        GroupMember groupMember = groupMemberDao.findByGroupIdAndUserIdValidated(groupId, userId);

        if (!groupMember.isCanInvite()) {
            throw ExceptionFactory.forbiddenOperation(userId + " must not invite members to groupId " + groupId);
        }

        Group group = groupDao.findByIdValidated(groupId);
        List<UUID> memberCandidates = groupMemberCandidateCollector.getCandidateUserIds(group);

        return accountClientProxy.search(query)
            .stream()
            .filter(accountResponse -> memberCandidates.contains(accountResponse.getUserId()))
            .map(accountResponseToSearchResultItemConverter::convert)
            .collect(Collectors.toList());
    }
}

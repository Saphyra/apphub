package com.github.saphyra.apphub.service.community.group.service.group_member;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.community.friendship.dao.friend.FriendshipDao;
import com.github.saphyra.apphub.service.community.group.dao.group.Group;
import com.github.saphyra.apphub.service.community.group.dao.member.GroupMember;
import com.github.saphyra.apphub.service.community.group.dao.member.GroupMemberDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class GroupMemberCandidateCollector {
    private final FriendshipDao friendshipDao;
    private final GroupMemberDao groupMemberDao;

    public List<UUID> getCandidateUserIds(Group group) {
        List<UUID> currentMembers = groupMemberDao.getByGroupId(group.getGroupId())
            .stream()
            .map(GroupMember::getUserId)
            .collect(Collectors.toList());

        return getCandidateUserIds(group, currentMembers);
    }

    public List<UUID> getCandidateUserIds(Group group, List<UUID> currentMembers) {
        switch (group.getInvitationType()) {
            case FRIENDS:
                return getFriendsOf(group.getOwnerId(), currentMembers);
            case FRIENDS_OF_FRIENDS:
                return getFriendsOfFriends(currentMembers);
            default:
                throw ExceptionFactory.loggedException(HttpStatus.NOT_IMPLEMENTED, ErrorCode.GENERAL_ERROR, group.getInvitationType() + " is not handled.");
        }
    }

    private List<UUID> getFriendsOfFriends(List<UUID> currentMembers) {
        return currentMembers.stream()
            .flatMap(userId -> getFriendsOf(userId, currentMembers).stream())
            .distinct()
            .filter(userId -> !currentMembers.contains(userId))
            .collect(Collectors.toList());
    }

    private List<UUID> getFriendsOf(UUID userId, List<UUID> currentMembers) {
        return friendshipDao.getByUserIdOrFriendId(userId)
            .stream()
            .map(friendship -> friendship.getOtherUserId(userId))
            .filter(id -> !currentMembers.contains(id))
            .collect(Collectors.toList());
    }
}

package com.github.saphyra.apphub.service.community.group.service.group_member;

import com.github.saphyra.apphub.api.community.model.response.group.GroupInvitationType;
import com.github.saphyra.apphub.service.community.friendship.dao.friend.Friendship;
import com.github.saphyra.apphub.service.community.friendship.dao.friend.FriendshipDao;
import com.github.saphyra.apphub.service.community.group.dao.group.Group;
import com.github.saphyra.apphub.service.community.group.dao.member.GroupMember;
import com.github.saphyra.apphub.service.community.group.dao.member.GroupMemberDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class GroupMemberCandidateCollectorTest {
    private static final UUID GROUP_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final UUID CANDIDATE_USER_ID = UUID.randomUUID();

    @Mock
    private FriendshipDao friendshipDao;

    @Mock
    private GroupMemberDao groupMemberDao;

    @InjectMocks
    private GroupMemberCandidateCollector underTest;

    @Mock
    private Group group;

    @Mock
    private GroupMember groupMember;

    @Mock
    private Friendship friendship1;

    @Mock
    private Friendship friendship2;

    @Test
    public void friendsOnly() {
        given(group.getGroupId()).willReturn(GROUP_ID);
        given(group.getInvitationType()).willReturn(GroupInvitationType.FRIENDS);
        given(groupMemberDao.getByGroupId(GROUP_ID)).willReturn(List.of(groupMember));
        given(groupMember.getUserId()).willReturn(USER_ID);
        given(group.getOwnerId()).willReturn(OWNER_ID);

        given(friendshipDao.getByUserIdOrFriendId(OWNER_ID)).willReturn(List.of(friendship1, friendship2));
        given(friendship1.getOtherUserId(OWNER_ID)).willReturn(USER_ID);
        given(friendship2.getOtherUserId(OWNER_ID)).willReturn(CANDIDATE_USER_ID);

        List<UUID> result = underTest.getCandidateUserIds(group);

        assertThat(result).containsExactly(CANDIDATE_USER_ID);
    }

    @Test
    public void friendsOfFriends() {
        given(group.getGroupId()).willReturn(GROUP_ID);
        given(group.getInvitationType()).willReturn(GroupInvitationType.FRIENDS_OF_FRIENDS);
        given(groupMemberDao.getByGroupId(GROUP_ID)).willReturn(List.of(groupMember));
        given(groupMember.getUserId()).willReturn(USER_ID);

        given(friendshipDao.getByUserIdOrFriendId(USER_ID)).willReturn(List.of(friendship1, friendship2));
        given(friendship1.getOtherUserId(USER_ID)).willReturn(USER_ID);
        given(friendship2.getOtherUserId(USER_ID)).willReturn(CANDIDATE_USER_ID);

        List<UUID> result = underTest.getCandidateUserIds(group);

        assertThat(result).containsExactly(CANDIDATE_USER_ID);
    }
}
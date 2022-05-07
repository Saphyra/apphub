package com.github.saphyra.apphub.service.community.group;

import com.github.saphyra.apphub.api.community.model.response.SearchResultItem;
import com.github.saphyra.apphub.api.community.model.response.group.GroupInvitationType;
import com.github.saphyra.apphub.api.community.model.response.group.GroupListResponse;
import com.github.saphyra.apphub.api.community.model.response.group.GroupMemberResponse;
import com.github.saphyra.apphub.api.community.model.response.group.GroupMemberRoleRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.community.group.service.group.GroupCreationService;
import com.github.saphyra.apphub.service.community.group.service.group.GroupDeletionService;
import com.github.saphyra.apphub.service.community.group.service.group.GroupEditionService;
import com.github.saphyra.apphub.service.community.group.service.group.GroupQueryService;
import com.github.saphyra.apphub.service.community.group.service.group_member.GroupMemberCandidateQueryService;
import com.github.saphyra.apphub.service.community.group.service.group_member.GroupMemberCreationService;
import com.github.saphyra.apphub.service.community.group.service.group_member.GroupMemberDeletionService;
import com.github.saphyra.apphub.service.community.group.service.group_member.GroupMemberQueryService;
import com.github.saphyra.apphub.service.community.group.service.group_member.GroupMemberRoleModificationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GroupControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String GROUP_NAME = "group-name";
    private static final UUID GROUP_ID = UUID.randomUUID();
    private static final UUID GROUP_MEMBER_ID = UUID.randomUUID();
    private static final String QUERY = "query";

    @Mock
    private GroupQueryService groupQueryService;

    @Mock
    private GroupCreationService groupCreationService;

    @Mock
    private GroupEditionService groupEditionService;

    @Mock
    private GroupMemberQueryService groupMemberQueryService;

    @Mock
    private GroupMemberCandidateQueryService groupMemberCandidateQueryService;

    @Mock
    private GroupMemberCreationService groupMemberCreationService;

    @Mock
    private GroupMemberDeletionService groupMemberDeletionService;

    @Mock
    private GroupMemberRoleModificationService groupMemberRoleModificationService;

    @Mock
    private GroupDeletionService groupDeletionService;

    @InjectMocks
    private GroupControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private GroupListResponse groupListResponse;

    @Mock
    private GroupMemberResponse groupMemberResponse;

    @Mock
    private SearchResultItem searchResultItem;

    @Mock
    private GroupMemberRoleRequest groupMemberRoleRequest;

    @Before
    public void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void getGroups() {
        given(groupQueryService.getGroups(USER_ID)).willReturn(List.of(groupListResponse));

        List<GroupListResponse> result = underTest.getGroups(accessTokenHeader);

        assertThat(result).containsExactly(groupListResponse);
    }

    @Test
    public void createGroup() {
        given(groupCreationService.create(USER_ID, GROUP_NAME)).willReturn(groupListResponse);

        GroupListResponse result = underTest.createGroup(new OneParamRequest<>(GROUP_NAME), accessTokenHeader);

        assertThat(result).isEqualTo(groupListResponse);
    }

    @Test
    public void deleteGroup() {
        underTest.deleteGroup(GROUP_ID, accessTokenHeader);

        verify(groupDeletionService).deleteGroup(USER_ID, GROUP_ID);
    }

    @Test
    public void changeOwner() {
        underTest.changeOwner(new OneParamRequest<>(GROUP_MEMBER_ID), GROUP_ID, accessTokenHeader);

        verify(groupEditionService).changeOwner(USER_ID, GROUP_ID, GROUP_MEMBER_ID);
    }

    @Test
    public void renameGroup() {
        given(groupEditionService.rename(USER_ID, GROUP_ID, GROUP_NAME)).willReturn(groupListResponse);

        GroupListResponse result = underTest.renameGroup(new OneParamRequest<>(GROUP_NAME), GROUP_ID, accessTokenHeader);

        assertThat(result).isEqualTo(groupListResponse);
    }

    @Test
    public void changeInvitationType() {
        given(groupEditionService.changeInvitationType(USER_ID, GROUP_ID, GroupInvitationType.FRIENDS_OF_FRIENDS)).willReturn(groupListResponse);

        GroupListResponse result = underTest.changeInvitationType(new OneParamRequest<>(GroupInvitationType.FRIENDS_OF_FRIENDS), GROUP_ID, accessTokenHeader);

        assertThat(result).isEqualTo(groupListResponse);
    }

    @Test
    public void getMembersOfGroup() {
        given(groupMemberQueryService.getMembers(USER_ID, GROUP_ID)).willReturn(List.of(groupMemberResponse));

        List<GroupMemberResponse> result = underTest.getMembersOfGroup(GROUP_ID, accessTokenHeader);

        assertThat(result).containsExactly(groupMemberResponse);
    }

    @Test
    public void searchMemberCandidates() {
        given(groupMemberCandidateQueryService.search(USER_ID, GROUP_ID, QUERY)).willReturn(List.of(searchResultItem));

        List<SearchResultItem> result = underTest.searchMemberCandidates(new OneParamRequest<>(QUERY), GROUP_ID, accessTokenHeader);

        assertThat(result).containsExactly(searchResultItem);
    }

    @Test
    public void createMember() {
        given(groupMemberCreationService.create(USER_ID, GROUP_ID, GROUP_MEMBER_ID)).willReturn(groupMemberResponse);

        GroupMemberResponse result = underTest.createMember(new OneParamRequest<>(GROUP_MEMBER_ID), GROUP_ID, accessTokenHeader);

        assertThat(result).isEqualTo(groupMemberResponse);
    }

    @Test
    public void deleteMember() {
        underTest.deleteMember(GROUP_ID, GROUP_MEMBER_ID, accessTokenHeader);

        verify(groupMemberDeletionService).delete(USER_ID, GROUP_ID, GROUP_MEMBER_ID);
    }

    @Test
    public void modifyRoles() {
        given(groupMemberRoleModificationService.modifyRoles(USER_ID, GROUP_ID, GROUP_MEMBER_ID, groupMemberRoleRequest)).willReturn(groupMemberResponse);

        GroupMemberResponse result = underTest.modifyRoles(groupMemberRoleRequest, GROUP_ID, GROUP_MEMBER_ID, accessTokenHeader);

        assertThat(result).isEqualTo(groupMemberResponse);
    }
}
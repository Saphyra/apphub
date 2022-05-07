package com.github.saphyra.apphub.service.community.group.service.group_member;

import com.github.saphyra.apphub.api.community.model.response.group.GroupMemberResponse;
import com.github.saphyra.apphub.api.user.model.response.AccountResponse;
import com.github.saphyra.apphub.service.community.common.AccountClientProxy;
import com.github.saphyra.apphub.service.community.group.dao.member.GroupMember;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class GroupMemberToResponseConverterTest {
    private static final UUID GROUP_MEMBER_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";

    @Mock
    private AccountClientProxy accountClientProxy;

    @InjectMocks
    private GroupMemberToResponseConverter underTest;

    @Mock
    private AccountResponse accountResponse;

    @Test
    public void convert() {
        GroupMember groupMember = GroupMember.builder()
            .groupMemberId(GROUP_MEMBER_ID)
            .userId(USER_ID)
            .canModifyRoles(true)
            .canInvite(true)
            .canKick(true)
            .build();

        given(accountClientProxy.getAccount(USER_ID)).willReturn(accountResponse);
        given(accountResponse.getUsername()).willReturn(USERNAME);
        given(accountResponse.getEmail()).willReturn(EMAIL);

        GroupMemberResponse result = underTest.convert(groupMember);

        assertThat(result.getGroupMemberId()).isEqualTo(GROUP_MEMBER_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getUsername()).isEqualTo(USERNAME);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
        assertThat(result.getCanInvite()).isTrue();
        assertThat(result.getCanKick()).isTrue();
        assertThat(result.getCanModifyRoles()).isTrue();
    }
}
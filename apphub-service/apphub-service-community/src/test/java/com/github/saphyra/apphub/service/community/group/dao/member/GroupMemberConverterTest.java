package com.github.saphyra.apphub.service.community.group.dao.member;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class GroupMemberConverterTest {
    private static final UUID GROUP_MEMBER_ID = UUID.randomUUID();
    private static final UUID GROUP_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String GROUP_MEMBER_ID_STRING = "group.member-id";
    private static final String GROUP_ID_STRING = "group-id";
    private static final String USER_ID_STRING = "user-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private GroupMemberConverter underTest;

    @Test
    public void convertDomain() {
        GroupMember domain = GroupMember.builder()
            .groupMemberId(GROUP_MEMBER_ID)
            .groupId(GROUP_ID)
            .userId(USER_ID)
            .canInvite(true)
            .canKick(true)
            .build();

        given(uuidConverter.convertDomain(GROUP_MEMBER_ID)).willReturn(GROUP_MEMBER_ID_STRING);
        given(uuidConverter.convertDomain(GROUP_ID)).willReturn(GROUP_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        GroupMemberEntity result = underTest.convertDomain(domain);

        assertThat(result.getGroupMemberId()).isEqualTo(GROUP_MEMBER_ID_STRING);
        assertThat(result.getGroupId()).isEqualTo(GROUP_ID_STRING);
        assertThat(result.getUserId()).isEqualTo(USER_ID_STRING);
        assertThat(result.getCanInvite()).isTrue();
        assertThat(result.getCanKick()).isTrue();
    }

    @Test
    public void convertEntity() {
        GroupMemberEntity entity = GroupMemberEntity.builder()
            .groupMemberId(GROUP_MEMBER_ID_STRING)
            .groupId(GROUP_ID_STRING)
            .userId(USER_ID_STRING)
            .canInvite(true)
            .canKick(true)
            .build();

        given(uuidConverter.convertEntity(GROUP_MEMBER_ID_STRING)).willReturn(GROUP_MEMBER_ID);
        given(uuidConverter.convertEntity(GROUP_ID_STRING)).willReturn(GROUP_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);

        GroupMember result = underTest.convertEntity(entity);

        assertThat(result.getGroupMemberId()).isEqualTo(GROUP_MEMBER_ID);
        assertThat(result.getGroupId()).isEqualTo(GROUP_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.isCanInvite()).isTrue();
        assertThat(result.isCanKick()).isTrue();
    }
}
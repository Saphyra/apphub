package com.github.saphyra.apphub.service.community.group.service.group_member;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
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
public class GroupMemberFactoryTest {
    private static final UUID GROUP_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID GROUP_MEMBER_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private GroupMemberFactory underTest;

    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(GROUP_MEMBER_ID);

        GroupMember result = underTest.create(GROUP_ID, USER_ID, true);

        assertThat(result.getGroupMemberId()).isEqualTo(GROUP_MEMBER_ID);
        assertThat(result.getGroupId()).isEqualTo(GROUP_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.isCanKick()).isTrue();
        assertThat(result.isCanInvite()).isTrue();
        assertThat(result.isCanModifyRoles()).isTrue();
    }
}
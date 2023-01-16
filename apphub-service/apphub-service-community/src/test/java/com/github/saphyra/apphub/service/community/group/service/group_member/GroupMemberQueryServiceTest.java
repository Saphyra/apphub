package com.github.saphyra.apphub.service.community.group.service.group_member;

import com.github.saphyra.apphub.api.community.model.response.group.GroupMemberResponse;
import com.github.saphyra.apphub.service.community.group.dao.member.GroupMember;
import com.github.saphyra.apphub.service.community.group.dao.member.GroupMemberDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class GroupMemberQueryServiceTest {
    private static final UUID GROUP_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private GroupMemberDao groupMemberDao;

    @Mock
    private GroupMemberToResponseConverter groupMemberToResponseConverter;

    @InjectMocks
    private GroupMemberQueryService underTest;

    @Mock
    private GroupMember groupMember;

    @Mock
    private GroupMemberResponse groupMemberResponse;

    @Test
    public void forbiddenOperation() {
        given(groupMemberDao.getByGroupId(GROUP_ID)).willReturn(List.of(groupMember));
        given(groupMember.getUserId()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.getMembers(USER_ID, GROUP_ID));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void getMembers() {
        given(groupMemberDao.getByGroupId(GROUP_ID)).willReturn(List.of(groupMember));
        given(groupMember.getUserId()).willReturn(USER_ID);
        given(groupMemberToResponseConverter.convert(groupMember)).willReturn(groupMemberResponse);

        List<GroupMemberResponse> result = underTest.getMembers(USER_ID, GROUP_ID);

        assertThat(result).containsExactly(groupMemberResponse);
    }
}
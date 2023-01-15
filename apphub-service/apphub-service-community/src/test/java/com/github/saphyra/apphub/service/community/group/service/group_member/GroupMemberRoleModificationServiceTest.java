package com.github.saphyra.apphub.service.community.group.service.group_member;

import com.github.saphyra.apphub.api.community.model.response.group.GroupMemberResponse;
import com.github.saphyra.apphub.api.community.model.response.group.GroupMemberRoleRequest;
import com.github.saphyra.apphub.service.community.group.dao.group.Group;
import com.github.saphyra.apphub.service.community.group.dao.group.GroupDao;
import com.github.saphyra.apphub.service.community.group.dao.member.GroupMember;
import com.github.saphyra.apphub.service.community.group.dao.member.GroupMemberDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GroupMemberRoleModificationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID GROUP_ID = UUID.randomUUID();
    private static final UUID GROUP_MEMBER_ID = UUID.randomUUID();
    private static final UUID OWNER_ID = UUID.randomUUID();

    @Mock
    private GroupMemberRoleRequestValidator groupMemberRoleRequestValidator;

    @Mock
    private GroupDao groupDao;

    @Mock
    private GroupMemberDao groupMemberDao;

    @Mock
    private GroupMemberToResponseConverter groupMemberToResponseConverter;

    @InjectMocks
    private GroupMemberRoleModificationService underTest;

    @Mock
    private GroupMemberRoleRequest request;

    @Mock
    private GroupMember groupMember;

    @Mock
    private GroupMember toModify;

    @Mock
    private Group group;

    @Mock
    private GroupMemberResponse groupMemberResponse;

    @AfterEach
    public void validate() {
        verify(groupMemberRoleRequestValidator).validate(request);
    }

    @Test
    public void forbiddenOperation() {
        given(groupMemberDao.findByGroupIdAndUserIdValidated(GROUP_ID, USER_ID)).willReturn(groupMember);
        given(groupMember.isCanModifyRoles()).willReturn(false);

        Throwable ex = catchThrowable(() -> underTest.modifyRoles(USER_ID, GROUP_ID, GROUP_MEMBER_ID, request));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void modifyGroupOwner() {
        given(groupMemberDao.findByGroupIdAndUserIdValidated(GROUP_ID, USER_ID)).willReturn(groupMember);
        given(groupMember.isCanModifyRoles()).willReturn(true);
        given(groupDao.findByIdValidated(GROUP_ID)).willReturn(group);
        given(group.getOwnerId()).willReturn(OWNER_ID);
        given(groupMemberDao.findByIdValidated(GROUP_MEMBER_ID)).willReturn(toModify);
        given(toModify.getUserId()).willReturn(OWNER_ID);

        Throwable ex = catchThrowable(() -> underTest.modifyRoles(USER_ID, GROUP_ID, GROUP_MEMBER_ID, request));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void modifyRoles() {
        given(groupMemberDao.findByGroupIdAndUserIdValidated(GROUP_ID, USER_ID)).willReturn(groupMember);
        given(groupMember.isCanModifyRoles()).willReturn(true);
        given(groupDao.findByIdValidated(GROUP_ID)).willReturn(group);
        given(group.getOwnerId()).willReturn(OWNER_ID);
        given(groupMemberDao.findByIdValidated(GROUP_MEMBER_ID)).willReturn(toModify);
        given(toModify.getUserId()).willReturn(UUID.randomUUID());

        given(request.getCanModifyRoles()).willReturn(true);
        given(request.getCanInvite()).willReturn(true);
        given(request.getCanKick()).willReturn(true);

        given(groupMemberToResponseConverter.convert(toModify)).willReturn(groupMemberResponse);

        GroupMemberResponse result = underTest.modifyRoles(USER_ID, GROUP_ID, GROUP_MEMBER_ID, request);

        assertThat(result).isEqualTo(groupMemberResponse);

        verify(toModify).setCanKick(true);
        verify(toModify).setCanModifyRoles(true);
        verify(toModify).setCanInvite(true);

        verify(groupMemberDao).save(toModify);
    }
}
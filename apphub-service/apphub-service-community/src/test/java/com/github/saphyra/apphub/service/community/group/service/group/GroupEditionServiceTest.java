package com.github.saphyra.apphub.service.community.group.service.group;

import com.github.saphyra.apphub.api.community.model.response.group.GroupInvitationType;
import com.github.saphyra.apphub.api.community.model.response.group.GroupListResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.community.group.dao.group.Group;
import com.github.saphyra.apphub.service.community.group.dao.group.GroupDao;
import com.github.saphyra.apphub.service.community.group.dao.member.GroupMember;
import com.github.saphyra.apphub.service.community.group.dao.member.GroupMemberDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GroupEditionServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID GROUP_ID = UUID.randomUUID();
    private static final String GROUP_NAME = "group-name";
    private static final UUID GROUP_MEMBER_ID = UUID.randomUUID();
    private static final UUID NEW_OWNER_ID = UUID.randomUUID();

    @Mock
    private GroupNameValidator groupNameValidator;

    @Mock
    private GroupDao groupDao;

    @Mock
    private GroupToResponseConverter groupToResponseConverter;

    @Mock
    private GroupMemberDao groupMemberDao;

    @InjectMocks
    private GroupEditionService underTest;

    @Mock
    private Group group;

    @Mock
    private GroupMember groupMember;

    @Mock
    private GroupListResponse groupListResponse;

    @Test
    public void rename_forbiddenOperation() {
        given(groupDao.findByIdValidated(GROUP_ID)).willReturn(group);
        given(group.getOwnerId()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.rename(USER_ID, GROUP_ID, GROUP_NAME));

        verify(groupNameValidator).validate(GROUP_NAME);

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void rename() {
        given(groupDao.findByIdValidated(GROUP_ID)).willReturn(group);
        given(group.getOwnerId()).willReturn(USER_ID);
        given(groupToResponseConverter.convert(group)).willReturn(groupListResponse);

        GroupListResponse result = underTest.rename(USER_ID, GROUP_ID, GROUP_NAME);

        verify(groupNameValidator).validate(GROUP_NAME);
        verify(group).setName(GROUP_NAME);
        verify(groupDao).save(group);

        assertThat(result).isEqualTo(groupListResponse);
    }

    @Test
    public void changeInvitationType_null() {
        Throwable ex = catchThrowable(() -> underTest.changeInvitationType(USER_ID, GROUP_ID, null));

        ExceptionValidator.validateInvalidParam(ex, "invitationType", "must not be null");
    }

    @Test
    public void changeInvitationType_forbiddenOperation() {
        given(groupDao.findByIdValidated(GROUP_ID)).willReturn(group);
        given(group.getOwnerId()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.changeInvitationType(USER_ID, GROUP_ID, GroupInvitationType.FRIENDS));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void changeInvitationType() {
        given(groupDao.findByIdValidated(GROUP_ID)).willReturn(group);
        given(group.getOwnerId()).willReturn(USER_ID);
        given(groupToResponseConverter.convert(group)).willReturn(groupListResponse);

        GroupListResponse result = underTest.changeInvitationType(USER_ID, GROUP_ID, GroupInvitationType.FRIENDS_OF_FRIENDS);

        verify(group).setInvitationType(GroupInvitationType.FRIENDS_OF_FRIENDS);
        verify(groupDao).save(group);

        assertThat(result).isEqualTo(groupListResponse);
    }

    @Test
    public void changeOwner_null() {
        Throwable ex = catchThrowable(() -> underTest.changeOwner(USER_ID, GROUP_ID, null));

        ExceptionValidator.validateInvalidParam(ex, "groupMemberId", "must not be null");
    }

    @Test
    public void changeOwner_forbiddenOperation() {
        given(groupDao.findByIdValidated(GROUP_ID)).willReturn(group);
        given(group.getOwnerId()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.changeOwner(USER_ID, GROUP_ID, GROUP_MEMBER_ID));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void changeOwner_alreadyOwner() {
        given(groupDao.findByIdValidated(GROUP_ID)).willReturn(group);
        given(group.getOwnerId()).willReturn(USER_ID);

        given(groupMemberDao.findByIdValidated(GROUP_MEMBER_ID)).willReturn(groupMember);
        given(groupMember.getUserId()).willReturn(USER_ID);

        Throwable ex = catchThrowable(() -> underTest.changeOwner(USER_ID, GROUP_ID, GROUP_MEMBER_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.CONFLICT, ErrorCode.GENERAL_ERROR);
    }

    @Test
    public void changeOwner() {
        given(groupDao.findByIdValidated(GROUP_ID)).willReturn(group);
        given(group.getOwnerId()).willReturn(USER_ID);

        given(groupMemberDao.findByIdValidated(GROUP_MEMBER_ID)).willReturn(groupMember);
        given(groupMember.getUserId()).willReturn(NEW_OWNER_ID);

        underTest.changeOwner(USER_ID, GROUP_ID, GROUP_MEMBER_ID);

        verify(group).setOwnerId(NEW_OWNER_ID);
        verify(groupDao).save(group);

        verify(groupMember).setCanKick(true);
        verify(groupMember).setCanInvite(true);
        verify(groupMember).setCanModifyRoles(true);
        verify(groupMemberDao).save(groupMember);
    }
}
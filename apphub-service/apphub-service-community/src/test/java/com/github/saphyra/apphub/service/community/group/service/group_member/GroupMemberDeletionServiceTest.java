package com.github.saphyra.apphub.service.community.group.service.group_member;

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

import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GroupMemberDeletionServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID GROUP_ID = UUID.randomUUID();
    private static final UUID GROUP_MEMBER_ID = UUID.randomUUID();
    private static final UUID OWNER_ID = UUID.randomUUID();

    @Mock
    private GroupDao groupDao;

    @Mock
    private GroupMemberDao groupMemberDao;

    @InjectMocks
    private GroupMemberDeletionService underTest;

    @Mock
    private GroupMember groupMember;

    @Mock
    private GroupMember memberToDelete;

    @Mock
    private Group group;

    @Test
    public void forbiddenOperation() {
        given(groupMemberDao.findByGroupIdAndUserIdValidated(GROUP_ID, USER_ID)).willReturn(groupMember);
        given(groupMemberDao.findByIdValidated(GROUP_MEMBER_ID)).willReturn(memberToDelete);
        given(groupMember.isCanKick()).willReturn(false);
        given(memberToDelete.getUserId()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.delete(USER_ID, GROUP_ID, GROUP_MEMBER_ID));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void groupOwnerCannotBeDeleted() {
        given(groupMemberDao.findByGroupIdAndUserIdValidated(GROUP_ID, USER_ID)).willReturn(groupMember);
        given(groupMemberDao.findByIdValidated(GROUP_MEMBER_ID)).willReturn(memberToDelete);
        given(groupMember.isCanKick()).willReturn(true);
        given(memberToDelete.getUserId()).willReturn(OWNER_ID);
        given(groupDao.findByIdValidated(GROUP_ID)).willReturn(group);
        given(group.getOwnerId()).willReturn(OWNER_ID);

        Throwable ex = catchThrowable(() -> underTest.delete(USER_ID, GROUP_ID, GROUP_MEMBER_ID));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void delete() {
        given(groupMemberDao.findByGroupIdAndUserIdValidated(GROUP_ID, USER_ID)).willReturn(groupMember);
        given(groupMemberDao.findByIdValidated(GROUP_MEMBER_ID)).willReturn(memberToDelete);
        given(groupMember.isCanKick()).willReturn(true);
        given(memberToDelete.getUserId()).willReturn(UUID.randomUUID());
        given(groupDao.findByIdValidated(GROUP_ID)).willReturn(group);
        given(group.getOwnerId()).willReturn(OWNER_ID);

        underTest.delete(USER_ID, GROUP_ID, GROUP_MEMBER_ID);

        verify(groupMemberDao).delete(memberToDelete);
    }

    @Test
    public void deleteOwn() {
        given(groupMemberDao.findByGroupIdAndUserIdValidated(GROUP_ID, USER_ID)).willReturn(groupMember);
        given(groupMemberDao.findByIdValidated(GROUP_MEMBER_ID)).willReturn(memberToDelete);
        given(groupMember.isCanKick()).willReturn(false);
        given(memberToDelete.getUserId()).willReturn(USER_ID);
        given(groupDao.findByIdValidated(GROUP_ID)).willReturn(group);
        given(group.getOwnerId()).willReturn(OWNER_ID);

        underTest.delete(USER_ID, GROUP_ID, GROUP_MEMBER_ID);

        verify(groupMemberDao).delete(memberToDelete);
    }
}
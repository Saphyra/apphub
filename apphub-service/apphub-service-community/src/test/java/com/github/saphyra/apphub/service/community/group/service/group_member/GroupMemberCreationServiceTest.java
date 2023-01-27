package com.github.saphyra.apphub.service.community.group.service.group_member;

import com.github.saphyra.apphub.api.community.model.response.group.GroupMemberResponse;
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

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
public class GroupMemberCreationServiceTest {
    private static final UUID GROUP_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID MEMBER_USER_ID = UUID.randomUUID();

    @Mock
    private GroupDao groupDao;

    @Mock
    private GroupMemberDao groupMemberDao;

    @Mock
    private GroupMemberCandidateCollector groupMemberCandidateCollector;

    @Mock
    private GroupMemberFactory groupMemberFactory;

    @Mock
    private GroupMemberToResponseConverter groupMemberToResponseConverter;

    @InjectMocks
    private GroupMemberCreationService underTest;

    @Mock
    private GroupMember groupMember;

    @Mock
    private Group group;

    @Mock
    private GroupMember newMember;

    @Mock
    private GroupMemberResponse groupMemberResponse;

    @Test
    public void nullInput() {
        Throwable ex = catchThrowable(() -> underTest.create(USER_ID, GROUP_ID, null));

        ExceptionValidator.validateInvalidParam(ex, "memberUserId", "must not be null");
    }

    @Test
    public void forbiddenOperation() {
        given(groupMemberDao.findByGroupIdAndUserIdValidated(GROUP_ID, USER_ID)).willReturn(groupMember);
        given(groupMember.isCanInvite()).willReturn(false);

        Throwable ex = catchThrowable(() -> underTest.create(USER_ID, GROUP_ID, MEMBER_USER_ID));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void alreadyMember() {
        given(groupMemberDao.findByGroupIdAndUserIdValidated(GROUP_ID, USER_ID)).willReturn(groupMember);
        given(groupMember.isCanInvite()).willReturn(true);
        given(groupDao.findByIdValidated(GROUP_ID)).willReturn(group);
        given(groupMemberCandidateCollector.getCandidateUserIds(group)).willReturn(Collections.emptyList());

        Throwable ex = catchThrowable(() -> underTest.create(USER_ID, GROUP_ID, MEMBER_USER_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.CONFLICT, ErrorCode.GENERAL_ERROR);
    }

    @Test
    public void create() {
        given(groupMemberDao.findByGroupIdAndUserIdValidated(GROUP_ID, USER_ID)).willReturn(groupMember);
        given(groupMember.isCanInvite()).willReturn(true);
        given(groupDao.findByIdValidated(GROUP_ID)).willReturn(group);
        given(groupMemberCandidateCollector.getCandidateUserIds(group)).willReturn(List.of(MEMBER_USER_ID));
        given(groupMemberFactory.create(GROUP_ID, MEMBER_USER_ID, false)).willReturn(newMember);
        given(groupMemberToResponseConverter.convert(newMember)).willReturn(groupMemberResponse);

        GroupMemberResponse result = underTest.create(USER_ID, GROUP_ID, MEMBER_USER_ID);

        verify(groupMemberDao).save(newMember);

        assertThat(result).isEqualTo(groupMemberResponse);
    }
}
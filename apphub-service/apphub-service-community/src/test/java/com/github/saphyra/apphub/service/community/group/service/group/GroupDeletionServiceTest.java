package com.github.saphyra.apphub.service.community.group.service.group;

import com.github.saphyra.apphub.service.community.group.dao.group.Group;
import com.github.saphyra.apphub.service.community.group.dao.group.GroupDao;
import com.github.saphyra.apphub.service.community.group.dao.member.GroupMemberDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GroupDeletionServiceTest {
    private static final UUID GROUP_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private GroupDao groupDao;

    @Mock
    private GroupMemberDao groupMemberDao;

    @InjectMocks
    private GroupDeletionService underTest;

    @Mock
    private Group group;

    @Test
    public void forbiddenOperation() {
        given(groupDao.findByIdValidated(GROUP_ID)).willReturn(group);

        given(group.getOwnerId()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.deleteGroup(USER_ID, GROUP_ID));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void deleteGroup() {
        given(groupDao.findByIdValidated(GROUP_ID)).willReturn(group);

        given(group.getOwnerId()).willReturn(USER_ID);

        underTest.deleteGroup(USER_ID, GROUP_ID);

        verify(groupDao).delete(group);
        verify(groupMemberDao).deleteByGroupId(GROUP_ID);
    }
}
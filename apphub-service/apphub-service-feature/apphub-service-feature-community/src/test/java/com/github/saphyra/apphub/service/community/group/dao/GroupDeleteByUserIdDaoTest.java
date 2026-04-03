package com.github.saphyra.apphub.service.community.group.dao;

import com.github.saphyra.apphub.service.community.group.dao.group.Group;
import com.github.saphyra.apphub.service.community.group.dao.group.GroupDao;
import com.github.saphyra.apphub.service.community.group.dao.member.GroupMemberDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GroupDeleteByUserIdDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID GROUP_ID = UUID.randomUUID();

    @Mock
    private GroupDao groupDao;

    @Mock
    private GroupMemberDao groupMemberDao;

    @InjectMocks
    private GroupDeleteByUserIdDao underTest;

    @Mock
    private Group group;

    @Test
    public void deleteByUserId() {
        given(groupDao.getByOwnerId(USER_ID)).willReturn(List.of(group));
        given(group.getGroupId()).willReturn(GROUP_ID);

        underTest.deleteByUserId(USER_ID);

        verify(groupMemberDao).deleteByGroupId(GROUP_ID);
        verify(groupDao).delete(group);
    }
}
package com.github.saphyra.apphub.service.community.group.service.group;

import com.github.saphyra.apphub.api.community.model.response.group.GroupListResponse;
import com.github.saphyra.apphub.service.community.group.dao.group.Group;
import com.github.saphyra.apphub.service.community.group.dao.group.GroupDao;
import com.github.saphyra.apphub.service.community.group.dao.member.GroupMember;
import com.github.saphyra.apphub.service.community.group.dao.member.GroupMemberDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class GroupQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID GROUP_ID = UUID.randomUUID();

    @Mock
    private GroupMemberDao groupMemberDao;

    @Mock
    private GroupDao groupDao;

    @Mock
    private GroupToResponseConverter groupToResponseConverter;

    @InjectMocks
    private GroupQueryService underTest;

    @Mock
    private Group group;

    @Mock
    private GroupMember groupMember;

    @Mock
    private GroupListResponse groupListResponse;

    @Test
    public void getGroups() {
        given(groupMemberDao.getByUserId(USER_ID)).willReturn(List.of(groupMember));
        given(groupMember.getGroupId()).willReturn(GROUP_ID);

        given(groupDao.findByIdValidated(GROUP_ID)).willReturn(group);
        given(groupToResponseConverter.convert(group)).willReturn(groupListResponse);

        List<GroupListResponse> result = underTest.getGroups(USER_ID);

        assertThat(result).containsExactly(groupListResponse);
    }
}
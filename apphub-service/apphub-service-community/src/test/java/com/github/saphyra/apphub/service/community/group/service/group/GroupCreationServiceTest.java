package com.github.saphyra.apphub.service.community.group.service.group;

import com.github.saphyra.apphub.api.community.model.response.group.GroupListResponse;
import com.github.saphyra.apphub.service.community.group.dao.group.Group;
import com.github.saphyra.apphub.service.community.group.dao.group.GroupDao;
import com.github.saphyra.apphub.service.community.group.dao.member.GroupMember;
import com.github.saphyra.apphub.service.community.group.dao.member.GroupMemberDao;
import com.github.saphyra.apphub.service.community.group.service.group_member.GroupMemberFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GroupCreationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String GROUP_NAME = "group-name";
    private static final UUID GROUP_ID = UUID.randomUUID();

    @Mock
    private GroupNameValidator groupNameValidator;

    @Mock
    private GroupFactory groupFactory;

    @Mock
    private GroupMemberFactory groupMemberFactory;

    @Mock
    private GroupDao groupDao;

    @Mock
    private GroupMemberDao groupMemberDao;

    @Mock
    private GroupToResponseConverter groupToResponseConverter;

    @InjectMocks
    private GroupCreationService underTest;

    @Mock
    private Group group;

    @Mock
    private GroupMember groupMember;

    @Mock
    private GroupListResponse groupListResponse;

    @Test
    public void create() {
        given(groupFactory.create(USER_ID, GROUP_NAME)).willReturn(group);
        given(group.getGroupId()).willReturn(GROUP_ID);
        given(groupMemberFactory.create(GROUP_ID, USER_ID, true)).willReturn(groupMember);
        given(groupToResponseConverter.convert(group)).willReturn(groupListResponse);

        GroupListResponse result = underTest.create(USER_ID, GROUP_NAME);

        verify(groupNameValidator).validate(GROUP_NAME);

        verify(groupDao).save(group);
        verify(groupMemberDao).save(groupMember);

        assertThat(result).isEqualTo(groupListResponse);
    }
}
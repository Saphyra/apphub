package com.github.saphyra.apphub.service.community.group.dao.group;

import com.github.saphyra.apphub.api.community.model.response.group.GroupInvitationType;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class GroupConverterTest {
    private static final UUID GROUP_ID = UUID.randomUUID();
    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final String NAME = "name";
    private static final String GROUP_ID_STRING = "group-id";
    private static final String OWNER_ID_STRING = "owner-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private GroupConverter underTest;

    @Test
    public void convertDomain() {
        Group group = Group.builder()
            .groupId(GROUP_ID)
            .ownerId(OWNER_ID)
            .name(NAME)
            .invitationType(GroupInvitationType.FRIENDS_OF_FRIENDS)
            .build();

        given(uuidConverter.convertDomain(GROUP_ID)).willReturn(GROUP_ID_STRING);
        given(uuidConverter.convertDomain(OWNER_ID)).willReturn(OWNER_ID_STRING);

        GroupEntity result = underTest.convertDomain(group);

        assertThat(result.getGroupId()).isEqualTo(GROUP_ID_STRING);
        assertThat(result.getOwnerId()).isEqualTo(OWNER_ID_STRING);
        assertThat(result.getName()).isEqualTo(NAME);
        assertThat(result.getInvitationType()).isEqualTo(GroupInvitationType.FRIENDS_OF_FRIENDS);
    }

    @Test
    public void convertEntity() {
        GroupEntity entity = GroupEntity.builder()
            .groupId(GROUP_ID_STRING)
            .ownerId(OWNER_ID_STRING)
            .name(NAME)
            .invitationType(GroupInvitationType.FRIENDS_OF_FRIENDS)
            .build();

        given(uuidConverter.convertEntity(GROUP_ID_STRING)).willReturn(GROUP_ID);
        given(uuidConverter.convertEntity(OWNER_ID_STRING)).willReturn(OWNER_ID);

        Group result = underTest.convertEntity(entity);

        assertThat(result.getGroupId()).isEqualTo(GROUP_ID);
        assertThat(result.getOwnerId()).isEqualTo(OWNER_ID);
        assertThat(result.getName()).isEqualTo(NAME);
        assertThat(result.getInvitationType()).isEqualTo(GroupInvitationType.FRIENDS_OF_FRIENDS);
    }
}
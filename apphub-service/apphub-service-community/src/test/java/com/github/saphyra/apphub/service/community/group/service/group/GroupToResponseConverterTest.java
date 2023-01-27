package com.github.saphyra.apphub.service.community.group.service.group;

import com.github.saphyra.apphub.api.community.model.response.group.GroupInvitationType;
import com.github.saphyra.apphub.api.community.model.response.group.GroupListResponse;
import com.github.saphyra.apphub.service.community.group.dao.group.Group;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class GroupToResponseConverterTest {
    private static final UUID GROUP_ID = UUID.randomUUID();
    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final String GROUP_NAME = "group-name";

    private final GroupToResponseConverter underTest = new GroupToResponseConverter();

    @Test
    public void convert() {
        Group group = Group.builder()
            .groupId(GROUP_ID)
            .ownerId(OWNER_ID)
            .name(GROUP_NAME)
            .invitationType(GroupInvitationType.FRIENDS_OF_FRIENDS)
            .build();

        GroupListResponse result = underTest.convert(group);

        assertThat(result.getGroupId()).isEqualTo(GROUP_ID);
        assertThat(result.getOwnerId()).isEqualTo(OWNER_ID);
        assertThat(result.getName()).isEqualTo(GROUP_NAME);
        assertThat(result.getInvitationType()).isEqualTo(GroupInvitationType.FRIENDS_OF_FRIENDS);
    }
}
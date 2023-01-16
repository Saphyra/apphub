package com.github.saphyra.apphub.service.community.group.service.group;

import com.github.saphyra.apphub.api.community.model.response.group.GroupInvitationType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.community.group.dao.group.Group;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class GroupFactoryTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String GROUP_NAME = "group-name";
    private static final UUID GROUP_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private GroupFactory underTest;

    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(GROUP_ID);

        Group result = underTest.create(USER_ID, GROUP_NAME);

        assertThat(result.getGroupId()).isEqualTo(GROUP_ID);
        assertThat(result.getOwnerId()).isEqualTo(USER_ID);
        assertThat(result.getName()).isEqualTo(GROUP_NAME);
        assertThat(result.getInvitationType()).isEqualTo(GroupInvitationType.FRIENDS);
    }
}
package com.github.saphyra.apphub.service.community.group.dao.group;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
public class GroupRepositoryTest {
    private static final String GROUP_ID_1 = "group-id-1";
    private static final String GROUP_ID_2 = "group-id-2";
    private static final String OWNER_ID_1 = "owner-id-1";
    private static final String OWNER_ID_2 = "owner-id-2";

    @Autowired
    private GroupRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    public void getByOwnerId() {
        GroupEntity entity1 = GroupEntity.builder()
            .groupId(GROUP_ID_1)
            .ownerId(OWNER_ID_1)
            .build();
        GroupEntity entity2 = GroupEntity.builder()
            .groupId(GROUP_ID_2)
            .ownerId(OWNER_ID_2)
            .build();
        underTest.saveAll(List.of(entity1, entity2));

        List<GroupEntity> result = underTest.getByOwnerId(OWNER_ID_1);

        assertThat(result).containsExactly(entity1);
    }
}
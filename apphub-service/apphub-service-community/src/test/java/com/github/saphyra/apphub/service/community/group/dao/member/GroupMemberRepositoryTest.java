package com.github.saphyra.apphub.service.community.group.dao.member;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
public class GroupMemberRepositoryTest {
    private static final String GROUP_MEMBER_ID_1 = "group-member-id-1";
    private static final String GROUP_MEMBER_ID_2 = "group-member-id-2";
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";
    private static final String GROUP_ID_1 = "group-id-1";
    private static final String GROUP_ID_2 = "group-id-2";

    @Autowired
    private GroupMemberRepository underTest;

    @After
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    public void deleteByUserId() {
        GroupMemberEntity entity1 = GroupMemberEntity.builder()
            .groupMemberId(GROUP_MEMBER_ID_1)
            .userId(USER_ID_1)
            .build();
        GroupMemberEntity entity2 = GroupMemberEntity.builder()
            .groupMemberId(GROUP_MEMBER_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.saveAll(List.of(entity1, entity2));

        underTest.deleteByUserId(USER_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    @Transactional
    public void deleteByGroupId() {
        GroupMemberEntity entity1 = GroupMemberEntity.builder()
            .groupMemberId(GROUP_MEMBER_ID_1)
            .groupId(GROUP_ID_1)
            .build();
        GroupMemberEntity entity2 = GroupMemberEntity.builder()
            .groupMemberId(GROUP_MEMBER_ID_2)
            .groupId(GROUP_ID_2)
            .build();
        underTest.saveAll(List.of(entity1, entity2));

        underTest.deleteByGroupId(GROUP_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }
}
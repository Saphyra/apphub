package com.github.saphyra.apphub.service.community.group.dao.member;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
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

    @AfterEach
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

    @Test
    public void getByUserId() {
        GroupMemberEntity entity1 = GroupMemberEntity.builder()
            .groupMemberId(GROUP_MEMBER_ID_1)
            .userId(USER_ID_1)
            .build();
        GroupMemberEntity entity2 = GroupMemberEntity.builder()
            .groupMemberId(GROUP_MEMBER_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.saveAll(List.of(entity1, entity2));

        List<GroupMemberEntity> result = underTest.getByUserId(USER_ID_1);

        assertThat(result).containsExactly(entity1);
    }

    @Test
    public void getByGroupId() {
        GroupMemberEntity entity1 = GroupMemberEntity.builder()
            .groupMemberId(GROUP_MEMBER_ID_1)
            .groupId(GROUP_ID_1)
            .build();
        GroupMemberEntity entity2 = GroupMemberEntity.builder()
            .groupMemberId(GROUP_MEMBER_ID_2)
            .groupId(GROUP_ID_2)
            .build();
        underTest.saveAll(List.of(entity1, entity2));

        List<GroupMemberEntity> result = underTest.getByGroupId(GROUP_ID_1);

        assertThat(result).containsExactly(entity1);
    }

    @Test
    public void findByGroupIdAndUserId() {
        GroupMemberEntity entity = GroupMemberEntity.builder()
            .groupMemberId(GROUP_MEMBER_ID_1)
            .userId(USER_ID_1)
            .groupId(GROUP_ID_1)
            .build();
        underTest.save(entity);

        Optional<GroupMemberEntity> result = underTest.findByGroupIdAndUserId(GROUP_ID_1, USER_ID_1);

        assertThat(result).contains(entity);
    }
}
package com.github.saphyra.apphub.service.user.data.dao.role;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
public class RoleRepositoryTest {
    private static final String ROLE_ID_1 = "role-id-1";
    private static final String ROLE_ID_2 = "role-id-2";
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";
    private static final String ROLE_1 = "role-1";
    private static final String ROLE_2 = "role-2";

    @Autowired
    private RoleRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    public void findByUserIdAndRole() {
        RoleEntity entity1 = RoleEntity.builder()
            .roleId(ROLE_ID_1)
            .userId(USER_ID_1)
            .role(ROLE_1)
            .build();
        RoleEntity entity2 = RoleEntity.builder()
            .roleId(ROLE_ID_2)
            .userId(USER_ID_2)
            .role(ROLE_1)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        Optional<RoleEntity> result = underTest.findByUserIdAndRole(USER_ID_1, ROLE_1);

        assertThat(result).contains(entity1);
    }

    @Test
    public void getByUserId() {
        RoleEntity entity1 = RoleEntity.builder()
            .roleId(ROLE_ID_1)
            .userId(USER_ID_1)
            .role(ROLE_1)
            .build();
        RoleEntity entity2 = RoleEntity.builder()
            .roleId(ROLE_ID_2)
            .userId(USER_ID_2)
            .role(ROLE_1)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        List<RoleEntity> result = underTest.getByUserId(USER_ID_1);

        assertThat(result).containsExactly(entity1);
    }

    @Test
    @Transactional
    public void deleteByUserId() {
        RoleEntity entity1 = RoleEntity.builder()
            .roleId(ROLE_ID_1)
            .userId(USER_ID_1)
            .role(ROLE_1)
            .build();
        RoleEntity entity2 = RoleEntity.builder()
            .roleId(ROLE_ID_2)
            .userId(USER_ID_2)
            .role(ROLE_1)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        underTest.deleteByUserId(USER_ID_2);

        assertThat(underTest.findAll()).containsExactly(entity1);
    }

    @Test
    @Transactional
    public void deleteByRole() {
        RoleEntity entity1 = RoleEntity.builder()
            .roleId(ROLE_ID_1)
            .userId(USER_ID_1)
            .role(ROLE_1)
            .build();
        RoleEntity entity2 = RoleEntity.builder()
            .roleId(ROLE_ID_2)
            .userId(USER_ID_2)
            .role(ROLE_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        underTest.deleteByRole(ROLE_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }
}
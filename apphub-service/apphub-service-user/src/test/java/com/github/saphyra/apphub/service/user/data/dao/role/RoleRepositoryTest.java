package com.github.saphyra.apphub.service.user.data.dao.role;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
public class RoleRepositoryTest {
    private static final String ROLE_ID_1 = "role-id-1";
    private static final String ROLE_ID_2 = "role-id-2";
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";
    private static final String ROLE = "role";

    @Autowired
    private RoleRepository underTest;

    @After
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    public void findByUserIdAndRole() {
        RoleEntity entity1 = RoleEntity.builder()
            .roleId(ROLE_ID_1)
            .userId(USER_ID_1)
            .role(ROLE)
            .build();
        RoleEntity entity2 = RoleEntity.builder()
            .roleId(ROLE_ID_2)
            .userId(USER_ID_2)
            .role(ROLE)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        Optional<RoleEntity> result = underTest.findByUserIdAndRole(USER_ID_1, ROLE);

        assertThat(result).contains(entity1);
    }

    @Test
    public void getByUserId() {
        RoleEntity entity1 = RoleEntity.builder()
            .roleId(ROLE_ID_1)
            .userId(USER_ID_1)
            .role(ROLE)
            .build();
        RoleEntity entity2 = RoleEntity.builder()
            .roleId(ROLE_ID_2)
            .userId(USER_ID_2)
            .role(ROLE)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        List<RoleEntity> result = underTest.getByUserId(USER_ID_1);

        assertThat(result).containsExactly(entity1);
    }
}
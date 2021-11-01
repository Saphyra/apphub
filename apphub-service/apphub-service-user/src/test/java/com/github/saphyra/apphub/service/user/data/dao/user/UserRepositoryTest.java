package com.github.saphyra.apphub.service.user.data.dao.user;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@Slf4j
public class UserRepositoryTest {
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";
    private static final String USER_ID_3 = "user-id-3";
    private static final String USER_ID_4 = "user-id-4";
    private static final String EMAIL_1 = "email-1";
    private static final String EMAIL_2 = "email-2";
    private static final String USERNAME_1 = "username-1";
    private static final String USERNAME_2 = "username-2";

    @Autowired
    private UserRepository underTest;

    @After
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    public void findByEmail() {
        UserEntity entity1 = UserEntity.builder()
            .userId(USER_ID_1)
            .email(EMAIL_1)
            .build();
        UserEntity entity2 = UserEntity.builder()
            .userId(USER_ID_2)
            .email(EMAIL_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        Optional<UserEntity> result = underTest.findByEmail(EMAIL_1);

        assertThat(result).contains(entity1);
    }

    @Test
    public void findByUsername() {
        UserEntity entity1 = UserEntity.builder()
            .userId(USER_ID_1)
            .username(USERNAME_1)
            .build();
        UserEntity entity2 = UserEntity.builder()
            .userId(USER_ID_2)
            .username(USERNAME_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        Optional<UserEntity> result = underTest.findByUsername(USERNAME_1);

        assertThat(result).contains(entity1);
    }

    @Test
    public void getByUsernameOrEmailContainingIgnoreCase() {
        UserEntity entity1 = UserEntity.builder()
            .userId(USER_ID_1)
            .username("adaSddf")
            .email("ad@as.ad")
            .build();
        UserEntity entity2 = UserEntity.builder()
            .userId(USER_ID_2)
            .username("ad")
            .email("adaSD@as.vfwr")
            .build();
        UserEntity entity3 = UserEntity.builder()
            .userId(USER_ID_3)
            .username("ad")
            .email("ad@as.ad")
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2, entity3));

        List<UserEntity> result = underTest.getByUsernameOrEmailContainingIgnoreCase("AsD");

        assertThat(result).containsExactlyInAnyOrder(entity1, entity2);
    }

    @Test
    public void getByUsersMarkedToDelete() {
        UserEntity entity1 = UserEntity.builder()
            .userId(USER_ID_1)
            .username("adaSddf")
            .email("ad@as.ad")
            .markedForDeletion(true)
            .build();
        UserEntity entity2 = UserEntity.builder()
            .userId(USER_ID_2)
            .username("ad")
            .email("adaSD@as.vfwr")
            .markedForDeletion(true)
            .markedForDeletionAt(LocalDateTime.now().withNano(0))
            .build();
        UserEntity entity3 = UserEntity.builder()
            .userId(USER_ID_3)
            .username("ad")
            .email("ad@as.ad")
            .build();
        UserEntity entity4 = UserEntity.builder()
            .userId(USER_ID_4)
            .username("ad")
            .email("adaSD@as.vfwr")
            .markedForDeletion(true)
            .markedForDeletionAt(LocalDateTime.now().minusSeconds(1).withNano(0))
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2, entity3, entity4));

        List<UserEntity> result = underTest.getByUsersMarkedToDelete();

        assertThat(result).containsExactly(entity4, entity2, entity1);
    }
}
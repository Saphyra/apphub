package com.github.saphyra.apphub.service.user.settings.dao;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import jakarta.transaction.Transactional;
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
public class UserSettingRepositoryTest {
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";
    private static final String CATEGORY_1 = "category-1";
    private static final String KEY = "key";
    private static final String VALUE = "value";

    @Autowired
    private UserSettingRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    public void getByUserIdAndCategory() {
        UserSettingEntity entity = UserSettingEntity.builder()
            .id(
                UserSettingEntityId.builder()
                    .userId(USER_ID_1)
                    .category(CATEGORY_1)
                    .key(KEY)
                    .build()
            )
            .value(VALUE)
            .build();

        underTest.save(entity);

        List<UserSettingEntity> result = underTest.getByUserIdAndCategory(USER_ID_1, CATEGORY_1);

        assertThat(result).containsExactly(entity);
    }

    @Test
    @Transactional
    void deleteByUserId() {
        UserSettingEntity entity1 = UserSettingEntity.builder()
            .id(
                UserSettingEntityId.builder()
                    .userId(USER_ID_1)
                    .category(CATEGORY_1)
                    .key(KEY)
                    .build()
            )
            .build();
        underTest.save(entity1);

        UserSettingEntity entity2 = UserSettingEntity.builder()
            .id(
                UserSettingEntityId.builder()
                    .userId(USER_ID_2)
                    .category(CATEGORY_1)
                    .key(KEY)
                    .build()
            )
            .build();
        underTest.save(entity2);

        underTest.deleteByUserId(USER_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }
}
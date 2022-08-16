package com.github.saphyra.apphub.service.user.settings.dao;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
public class UserSettingRepositoryTest {
    private static final String USER_ID_1 = "user-id-1";
    private static final String CATEGORY_1 = "category-1";
    private static final String KEY = "key";
    private static final String VALUE = "value";

    @Autowired
    private UserSettingRepository underTest;

    @After
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
}
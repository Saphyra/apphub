package com.github.saphyra.apphub.service.modules.dao.favorite;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import org.junit.After;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
public class FavoriteRepositoryTest {
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";
    private static final String MODULE_1 = "module-1";
    private static final String MODULE_2 = "module-2";

    @Autowired
    private FavoriteRepository underTest;

    @After
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    public void getByUserId() {
        FavoriteEntity entity1 = createEntity(USER_ID_1, MODULE_1);
        FavoriteEntity entity2 = createEntity(USER_ID_1, MODULE_2);
        FavoriteEntity entity3 = createEntity(USER_ID_2, MODULE_1);
        underTest.saveAll(Arrays.asList(entity1, entity2, entity3));

        List<FavoriteEntity> result = underTest.getByUserId(USER_ID_1);

        assertThat(result).containsExactlyInAnyOrder(entity1, entity2);
    }

    @Test
    public void deleteByUserId() {
        FavoriteEntity entity1 = createEntity(USER_ID_1, MODULE_1);
        FavoriteEntity entity2 = createEntity(USER_ID_1, MODULE_2);
        FavoriteEntity entity3 = createEntity(USER_ID_2, MODULE_1);
        underTest.saveAll(Arrays.asList(entity1, entity2, entity3));

        underTest.deleteByUserId(USER_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity3);
    }

    private FavoriteEntity createEntity(String userId, String module) {
        return FavoriteEntity.builder()
            .key(
                FavoriteEntityKey.builder()
                    .userId(userId)
                    .module(module)
                    .build()
            )
            .build();
    }
}
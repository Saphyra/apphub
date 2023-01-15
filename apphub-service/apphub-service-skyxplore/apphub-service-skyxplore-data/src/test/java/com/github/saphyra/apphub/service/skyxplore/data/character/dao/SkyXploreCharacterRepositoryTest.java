package com.github.saphyra.apphub.service.skyxplore.data.character.dao;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@Slf4j
public class SkyXploreCharacterRepositoryTest {
    private static final String CHARACTER_NAME = "character-name";
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";

    @Autowired
    private SkyXploreCharacterRepository underTest;

    @After
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    public void findByName() {
        SkyXploreCharacterEntity entity = SkyXploreCharacterEntity.builder()
            .userId(USER_ID_1)
            .name(CHARACTER_NAME)
            .build();
        underTest.save(entity);

        Optional<SkyXploreCharacterEntity> result = underTest.findByName(CHARACTER_NAME);

        assertThat(result).contains(entity);
    }

    @Test
    public void getByNameContainingIgnoreCase() {
        SkyXploreCharacterEntity entity1 = SkyXploreCharacterEntity.builder()
            .userId(USER_ID_1)
            .name(CHARACTER_NAME)
            .build();
        underTest.save(entity1);

        SkyXploreCharacterEntity entity2 = SkyXploreCharacterEntity.builder()
            .userId(USER_ID_2)
            .name(" a fwe")
            .build();
        underTest.save(entity2);

        List<SkyXploreCharacterEntity> result = underTest.getByNameContainingIgnoreCase("r-N");

        assertThat(result).containsExactly(entity1);
    }
}
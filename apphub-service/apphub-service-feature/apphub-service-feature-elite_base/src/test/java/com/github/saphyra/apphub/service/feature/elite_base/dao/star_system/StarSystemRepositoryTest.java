package com.github.saphyra.apphub.service.feature.elite_base.dao.star_system;

import com.github.saphyra.apphub.lib.test.repository.RepositoryTestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@Slf4j
class StarSystemRepositoryTest {
    private static final String ID_1 = "id-1";
    private static final String ID_2 = "id-2";
    private static final String ID_3 = "id-3";
    private static final String STAR_NAME = "star-name";

    @Autowired
    private StarSystemRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    void findByStarName() {
        StarSystemEntity entity = StarSystemEntity.builder()
            .id(ID_1)
            .starName(STAR_NAME)
            .build();
        underTest.save(entity);

        assertThat(underTest.findByStarName(STAR_NAME)).contains(entity);
    }

    @Test
    void getByStarNameIgnoreCaseContaining() {
        StarSystemEntity entity1 = StarSystemEntity.builder()
            .id(ID_1)
            .starName("incorrect")
            .build();
        underTest.save(entity1);

        Stream.iterate(2, i -> i + 1)
            .limit(11).forEach(i -> {
                StarSystemEntity entity = StarSystemEntity.builder()
                    .id("id-" + i)
                    .starName("star_name_" + i)
                    .build();
                underTest.save(entity);
            });

        assertThat(underTest.getByStarNameIgnoreCaseContaining("tar_NAME"))
            .hasSize(10)
            .doesNotContain(entity1);
    }

    @Test
    void deleteAllById() {
        StarSystemEntity entity1 = StarSystemEntity.builder()
            .id(ID_1)
            .build();
        underTest.save(entity1);
        StarSystemEntity entity2 = StarSystemEntity.builder()
            .id(ID_2)
            .build();
        underTest.save(entity2);
        StarSystemEntity entity3 = StarSystemEntity.builder()
            .id(ID_3)
            .build();
        underTest.save(entity3);

        underTest.deleteAllById(List.of(ID_1, ID_2));

        assertThat(underTest.findAll()).containsExactly(entity3);
    }
}
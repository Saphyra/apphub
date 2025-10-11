package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
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
    private static final String ID_4 = "id-4";
    private static final Long STAR_ID_1 = 34L;
    private static final Long STAR_ID_2 = 35L;
    private static final Long STAR_ID_3 = 36L;
    private static final String STAR_NAME_1 = "star-name-1";
    private static final String STAR_NAME_2 = "star-name-2";
    private static final String STAR_NAME_3 = "star-name-3";

    @Autowired
    private StarSystemRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    void findByStarId() {
        StarSystemEntity entity = StarSystemEntity.builder()
            .id(ID_1)
            .starId(STAR_ID_1)
            .build();
        underTest.save(entity);

        assertThat(underTest.findByStarId(STAR_ID_1)).contains(entity);
    }

    @Test
    void findByStarName() {
        StarSystemEntity entity = StarSystemEntity.builder()
            .id(ID_1)
            .starName(STAR_NAME_1)
            .build();
        underTest.save(entity);

        assertThat(underTest.findByStarName(STAR_NAME_1)).contains(entity);
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

        assertThat(underTest.getByStarNameIgnoreCaseContaining("star_NAME"))
            .hasSize(10)
            .doesNotContain(entity1);
    }

    @Test
    void getByStarIdOrStarName_doubleMatch() {
        StarSystemEntity entity1 = StarSystemEntity.builder()
            .id(ID_1)
            .starId(STAR_ID_1)
            .starName(STAR_NAME_1)
            .build();
        underTest.save(entity1);
        StarSystemEntity entity2 = StarSystemEntity.builder()
            .id(ID_2)
            .starId(STAR_ID_2)
            .starName(STAR_NAME_2)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByStarIdOrStarName(STAR_ID_1, STAR_NAME_1)).containsExactlyInAnyOrder(entity1);
    }

    @Test
    void getByStarIdOrStarName_starIdMatch() {
        StarSystemEntity entity1 = StarSystemEntity.builder()
            .id(ID_1)
            .starId(STAR_ID_1)
            .starName(STAR_NAME_2)
            .build();
        underTest.save(entity1);
        StarSystemEntity entity2 = StarSystemEntity.builder()
            .id(ID_2)
            .starId(STAR_ID_2)
            .starName(STAR_NAME_3)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByStarIdOrStarName(STAR_ID_1, STAR_NAME_1)).containsExactlyInAnyOrder(entity1);
    }

    @Test
    void getByStarIdOrStarName_starNameMatch() {
        StarSystemEntity entity1 = StarSystemEntity.builder()
            .id(ID_1)
            .starId(STAR_ID_2)
            .starName(STAR_NAME_1)
            .build();
        underTest.save(entity1);
        StarSystemEntity entity2 = StarSystemEntity.builder()
            .id(ID_2)
            .starId(STAR_ID_3)
            .starName(STAR_NAME_3)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByStarIdOrStarName(STAR_ID_1, STAR_NAME_1)).containsExactlyInAnyOrder(entity1);
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
package com.github.saphyra.apphub.service.feature.elite_base.dao.body;

import com.github.saphyra.apphub.lib.sql_builder.SqlBuilder;
import com.github.saphyra.apphub.lib.sql_builder.table.QualifiedTable;
import com.github.saphyra.apphub.lib.test.repository.RepositoryTestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;

import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_STAR_SYSTEM;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@Slf4j
class BodyRepositoryTest {
    private static final String ID_1 = "id-1";
    private static final String ID_2 = "id-2";
    private static final String ID_3 = "id-3";
    private static final String STAR_SYSTEM_ID_1 = "star-system-id-1";
    private static final String STAR_SYSTEM_ID_2 = "star-system-id-2";
    private static final String STAR_SYSTEM_ID_3 = "star-system-id-3";
    private static final String BODY_NAME = "body-name";

    @Autowired
    private BodyRepository underTest;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp(){
        saveStarSystem(STAR_SYSTEM_ID_1);
        saveStarSystem(STAR_SYSTEM_ID_2);
        saveStarSystem(STAR_SYSTEM_ID_3);
    }

    @AfterEach
    void clear() {
        underTest.deleteAll();

        jdbcTemplate.update(SqlBuilder.delete().from(new QualifiedTable(SCHEMA, TABLE_STAR_SYSTEM)).build(), Map.of());
    }

    @Test
    void findByBodyName() {
        BodyEntity entity = BodyEntity.builder()
            .id(ID_1)
            .bodyName(BODY_NAME)
            .build();
        underTest.save(entity);

        assertThat(underTest.findByBodyName(BODY_NAME)).contains(entity);
    }

    @Test
    void getByStarSystemIdIn() {
        BodyEntity entity1 = BodyEntity.builder()
            .id(ID_1)
            .starSystemId(STAR_SYSTEM_ID_1)
            .build();
        underTest.save(entity1);

        BodyEntity entity2 = BodyEntity.builder()
            .id(ID_2)
            .starSystemId(STAR_SYSTEM_ID_2)
            .build();
        underTest.save(entity2);

        BodyEntity entity3 = BodyEntity.builder()
            .id(ID_3)
            .starSystemId(STAR_SYSTEM_ID_3)
            .build();
        underTest.save(entity3);

        assertThat(underTest.getByStarSystemIdIn(List.of(STAR_SYSTEM_ID_1, STAR_SYSTEM_ID_2))).containsExactlyInAnyOrder(entity1, entity2);
    }

    private void saveStarSystem(String starSystemId) {
        Map<String, String> data = Map.of(COLUMN_ID, starSystemId);

        String sql = SqlBuilder.insert(new QualifiedTable(SCHEMA, TABLE_STAR_SYSTEM), data.keySet())
            .build();

        jdbcTemplate.update(sql, data);
    }
}
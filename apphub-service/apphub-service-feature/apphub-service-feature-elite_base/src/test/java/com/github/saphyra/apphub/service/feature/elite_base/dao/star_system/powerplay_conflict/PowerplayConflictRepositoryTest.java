package com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.powerplay_conflict;

import com.github.saphyra.apphub.lib.sql_builder.SqlBuilder;
import com.github.saphyra.apphub.lib.sql_builder.table.QualifiedTable;
import com.github.saphyra.apphub.lib.test.repository.RepositoryTestConfiguration;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.star_system_data.Power;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_STAR_SYSTEM;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
class PowerplayConflictRepositoryTest {
    private static final String STAR_SYSTEM_ID_1 = "star-system-id-1";
    private static final String STAR_SYSTEM_ID_2 = "star-system-id-2";

    @Autowired
    private PowerplayConflictRepository underTest;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp(){
        saveStarSystem(STAR_SYSTEM_ID_1);
        saveStarSystem(STAR_SYSTEM_ID_2);
    }

    @AfterEach
    void clear() {
        underTest.deleteAll();

        jdbcTemplate.update(SqlBuilder.delete().from(new QualifiedTable(SCHEMA, TABLE_STAR_SYSTEM)).build(), Map.of());
    }

    @Test
    void getByIdStarSystemId() {
        PowerplayConflictEntity entity1 = PowerplayConflictEntity.builder()
            .id(PowerplayConflictEntityId.builder()
                .starSystemId(STAR_SYSTEM_ID_1)
                .power(Power.NAKATO_KAINE)
                .build())
            .build();
        underTest.save(entity1);

        PowerplayConflictEntity entity2 = PowerplayConflictEntity.builder()
            .id(PowerplayConflictEntityId.builder()
                .starSystemId(STAR_SYSTEM_ID_2)
                .power(Power.NAKATO_KAINE)
                .build())
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByIdStarSystemId(STAR_SYSTEM_ID_1)).containsExactly(entity1);
    }

    private void saveStarSystem(String starSystemId) {
        Map<String, String> data = Map.of(COLUMN_ID, starSystemId);

        String sql = SqlBuilder.insert(new QualifiedTable(SCHEMA, TABLE_STAR_SYSTEM), data.keySet())
            .build();

        jdbcTemplate.update(sql, data);
    }
}
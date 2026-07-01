package com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.power;

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

import java.util.List;
import java.util.Map;

import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_STAR_SYSTEM;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
class StarSystemPowerMappingRepositoryTest {
    private static final String STAR_SYSTEM_ID_1 = "star-system-id-1";
    private static final String STAR_SYSTEM_ID_2 = "star-system-id-2";
    private static final String STAR_SYSTEM_ID_3 = "star-system-id-3";

    @Autowired
    private StarSystemPowerMappingRepository underTest;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        saveStarSystem(STAR_SYSTEM_ID_1);
        saveStarSystem(STAR_SYSTEM_ID_2);
        saveStarSystem(STAR_SYSTEM_ID_3);
    }

    @AfterEach
    public void clear() {
        underTest.deleteAll();
        jdbcTemplate.update(SqlBuilder.delete().from(new QualifiedTable(SCHEMA, TABLE_STAR_SYSTEM)).build(), Map.of());
    }

    @Test
    void getByStarSystemId() {
        StarSystemPowerMappingEntity entity1 = StarSystemPowerMappingEntity.builder()
            .starSystemId(STAR_SYSTEM_ID_1)
            .power(Power.AISLING_DUVAL)
            .build();
        underTest.save(entity1);
        StarSystemPowerMappingEntity entity2 = StarSystemPowerMappingEntity.builder()
            .starSystemId(STAR_SYSTEM_ID_2)
            .power(Power.AISLING_DUVAL)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByStarSystemId(STAR_SYSTEM_ID_1)).containsExactly(entity1);
    }

    @Test
    void getByStarSystemIds() {
        StarSystemPowerMappingEntity entity1 = StarSystemPowerMappingEntity.builder()
            .starSystemId(STAR_SYSTEM_ID_1)
            .power(Power.AISLING_DUVAL)
            .build();
        underTest.save(entity1);
        StarSystemPowerMappingEntity entity2 = StarSystemPowerMappingEntity.builder()
            .starSystemId(STAR_SYSTEM_ID_2)
            .power(Power.AISLING_DUVAL)
            .build();
        underTest.save(entity2);
        StarSystemPowerMappingEntity entity3 = StarSystemPowerMappingEntity.builder()
            .starSystemId(STAR_SYSTEM_ID_3)
            .power(Power.AISLING_DUVAL)
            .build();
        underTest.save(entity3);

        assertThat(underTest.getByStarSystemIds(List.of(STAR_SYSTEM_ID_1, STAR_SYSTEM_ID_2))).containsExactlyInAnyOrder(entity1, entity2);
    }

    private void saveStarSystem(String starSystemId) {
        Map<String, String> data = Map.of(COLUMN_ID, starSystemId);

        String sql = SqlBuilder.insert(new QualifiedTable(SCHEMA, TABLE_STAR_SYSTEM), data.keySet())
            .build();

        jdbcTemplate.update(sql, data);
    }
}
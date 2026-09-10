package com.github.saphyra.apphub.service.feature.elite_base.dao.minor_faction.state;

import com.github.saphyra.apphub.lib.sql_builder.SqlBuilder;
import com.github.saphyra.apphub.lib.sql_builder.table.QualifiedTable;
import com.github.saphyra.apphub.lib.test.repository.RepositoryTestConfiguration;
import com.github.saphyra.apphub.service.feature.elite_base.dao.FactionStateEnum;
import lombok.extern.slf4j.Slf4j;
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
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_MINOR_FACTION;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@Slf4j
class MinorFactionStateRepositoryTest {
    private static final String MINOR_FACTION_ID_1 = "minor-faction-id-1";
    private static final String MINOR_FACTION_ID_2 = "minor-faction-id-2";

    @Autowired
    private MinorFactionStateRepository underTest;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp(){
        saveMinorFaction(MINOR_FACTION_ID_1);
        saveMinorFaction(MINOR_FACTION_ID_2);
    }

    @AfterEach
    public void clear() {
        underTest.deleteAll();

        jdbcTemplate.update(SqlBuilder.delete().from(new QualifiedTable(SCHEMA, TABLE_MINOR_FACTION)).build(), Map.of());
    }

    @Test
    void getByMinorFactionIdAndStatus() {
        MinorFactionStateEntity entity1 = MinorFactionStateEntity.builder()
            .minorFactionId(MINOR_FACTION_ID_1)
            .status(StateStatus.ACTIVE)
            .state(FactionStateEnum.BLIGHT)
            .build();
        underTest.save(entity1);
        MinorFactionStateEntity entity2 = MinorFactionStateEntity.builder()
            .minorFactionId(MINOR_FACTION_ID_2)
            .status(StateStatus.ACTIVE)
            .state(FactionStateEnum.BLIGHT)
            .build();
        underTest.save(entity2);
        MinorFactionStateEntity entity3 = MinorFactionStateEntity.builder()
            .minorFactionId(MINOR_FACTION_ID_1)
            .status(StateStatus.PENDING)
            .state(FactionStateEnum.BLIGHT)
            .build();
        underTest.save(entity3);

        assertThat(underTest.getByMinorFactionIdAndStatus(MINOR_FACTION_ID_1, StateStatus.ACTIVE)).containsExactly(entity1);
    }

    @Test
    void getByMinorFactionId() {
        MinorFactionStateEntity entity1 = MinorFactionStateEntity.builder()
            .minorFactionId(MINOR_FACTION_ID_1)
            .status(StateStatus.ACTIVE)
            .state(FactionStateEnum.BLIGHT)
            .build();
        underTest.save(entity1);
        MinorFactionStateEntity entity2 = MinorFactionStateEntity.builder()
            .minorFactionId(MINOR_FACTION_ID_2)
            .status(StateStatus.ACTIVE)
            .state(FactionStateEnum.BLIGHT)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByMinorFactionId(MINOR_FACTION_ID_1)).containsExactly(entity1);
    }

    private void saveMinorFaction(String minorFactionId) {
        Map<String, String> data = Map.of(COLUMN_ID, minorFactionId);

        String sql = SqlBuilder.insert(new QualifiedTable(SCHEMA, TABLE_MINOR_FACTION), data.keySet())
            .build();

        jdbcTemplate.update(sql, data);
    }
}
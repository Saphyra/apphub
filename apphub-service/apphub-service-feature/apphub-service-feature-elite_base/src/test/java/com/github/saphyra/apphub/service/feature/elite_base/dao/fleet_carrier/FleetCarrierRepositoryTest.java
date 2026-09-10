package com.github.saphyra.apphub.service.feature.elite_base.dao.fleet_carrier;

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

import java.util.Map;

import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_STAR_SYSTEM;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@Slf4j
class FleetCarrierRepositoryTest {
    private static final String ID_1 = "id-1";
    private static final String ID_2 = "id-2";
    private static final String CARRIER_ID_1 = "carrier-id-1";
    private static final Long MARKET_ID_1 = 324L;
    private static final Long MARKET_ID_2 = 325L;
    private static final String STAR_SYSTEM_ID = "star-system-id";

    @Autowired
    private FleetCarrierRepository underTest;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        saveStarSystem(STAR_SYSTEM_ID);
    }

    @AfterEach
    public void clear() {
        underTest.deleteAll();

        jdbcTemplate.update(SqlBuilder.delete().from(new QualifiedTable(SCHEMA, TABLE_STAR_SYSTEM)).build(), Map.of());
    }

    @Test
    void findByCarrierId() {
        FleetCarrierEntity entity = FleetCarrierEntity.builder()
            .id(ID_1)
            .carrierId(CARRIER_ID_1)
            .starSystemId(STAR_SYSTEM_ID)
            .build();
        underTest.save(entity);

        assertThat(underTest.findByCarrierId(CARRIER_ID_1)).contains(entity);
    }

    @Test
    void findByMarketId() {
        FleetCarrierEntity entity = FleetCarrierEntity.builder()
            .id(ID_1)
            .marketId(MARKET_ID_1)
            .starSystemId(STAR_SYSTEM_ID)
            .build();
        underTest.save(entity);

        assertThat(underTest.findByMarketId(MARKET_ID_1)).contains(entity);
    }

    @Test
    void clearMarketId_doNotDelete() {
        FleetCarrierEntity entity1 = FleetCarrierEntity.builder()
            .id(ID_1)
            .marketId(MARKET_ID_1)
            .starSystemId(STAR_SYSTEM_ID)
            .build();
        underTest.save(entity1);
        FleetCarrierEntity entity2 = FleetCarrierEntity.builder()
            .id(ID_2)
            .marketId(MARKET_ID_2)
            .starSystemId(STAR_SYSTEM_ID)
            .build();
        underTest.save(entity2);

        underTest.clearMarketId(ID_1, MARKET_ID_1);

        assertThat(underTest.findAll()).containsExactlyInAnyOrder(entity1, entity2);
    }

    @Test
    void clearMarketId_delete() {
        FleetCarrierEntity entity1 = FleetCarrierEntity.builder()
            .id(ID_1)
            .marketId(MARKET_ID_1)
            .starSystemId(STAR_SYSTEM_ID)
            .build();
        underTest.save(entity1);

        underTest.clearMarketId(ID_2, MARKET_ID_1);

        assertThat(underTest.findById(ID_1))
            .isNotEmpty()
            .get()
            .returns(null, FleetCarrierEntity::getMarketId);
    }

    private void saveStarSystem(String starSystemId) {
        Map<String, String> data = Map.of(COLUMN_ID, starSystemId);

        String sql = SqlBuilder.insert(new QualifiedTable(SCHEMA, TABLE_STAR_SYSTEM), data.keySet())
            .build();

        jdbcTemplate.update(sql, data);
    }
}
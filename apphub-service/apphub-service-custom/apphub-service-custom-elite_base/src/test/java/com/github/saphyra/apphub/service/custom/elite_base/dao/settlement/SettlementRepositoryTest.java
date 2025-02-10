package com.github.saphyra.apphub.service.custom.elite_base.dao.settlement;

import com.github.saphyra.apphub.service.custom.elite_base.dao.settlement.SettlementEntity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.settlement.SettlementRepository;
import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@Slf4j
class SettlementRepositoryTest {
    private static final String ID_1 = "id-1";
    private static final String SETTLEMENT_NAME_1 = "settlement-name-1";
    private static final String STAR_SYSTEM_ID_1 = "star-system-id-1";

    @Autowired
    private SettlementRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    void findByStarSystemIdAndSettlementName() {
        SettlementEntity entity = SettlementEntity.builder()
            .id(ID_1)
            .settlementName(SETTLEMENT_NAME_1)
            .starSystemId(STAR_SYSTEM_ID_1)
            .build();
        underTest.save(entity);

        assertThat(underTest.findByStarSystemIdAndSettlementName(STAR_SYSTEM_ID_1, SETTLEMENT_NAME_1)).contains(entity);
    }
}
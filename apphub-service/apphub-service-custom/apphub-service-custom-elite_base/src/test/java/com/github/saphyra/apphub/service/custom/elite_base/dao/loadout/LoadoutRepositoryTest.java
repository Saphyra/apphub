package com.github.saphyra.apphub.service.custom.elite_base.dao.loadout;

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
class LoadoutRepositoryTest {
    private static final String EXTERNAL_REFERENCE_1 = "external-reference-1";
    private static final String EXTERNAL_REFERENCE_2 = "external-reference-2";
    private static final String NAME_1 = "name-1";
    private static final String NAME_2 = "name-2";
    private static final Long MARKET_ID_1 = 1L;
    private static final Long MARKET_ID_2 = 2L;

    @Autowired
    private LoadoutRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    void getByMarketIdAndType() {
        LoadoutEntity entity1 = LoadoutEntity.builder()
            .externalReference(EXTERNAL_REFERENCE_1)
            .type(LoadoutType.OUTFITTING)
            .name(NAME_1)
            .marketId(MARKET_ID_1)
            .type(LoadoutType.OUTFITTING)
            .build();
        underTest.save(entity1);
        LoadoutEntity entity2 = LoadoutEntity.builder()
            .externalReference(EXTERNAL_REFERENCE_2)
            .type(LoadoutType.OUTFITTING)
            .name(NAME_1)
            .marketId(MARKET_ID_1)
            .type(LoadoutType.SHIPYARD)
            .build();
        underTest.save(entity2);
        LoadoutEntity entity3 = LoadoutEntity.builder()
            .externalReference(EXTERNAL_REFERENCE_1)
            .type(LoadoutType.OUTFITTING)
            .name(NAME_2)
            .marketId(MARKET_ID_2)
            .type(LoadoutType.OUTFITTING)
            .build();
        underTest.save(entity3);


        assertThat(underTest.getByMarketIdAndType(MARKET_ID_1, LoadoutType.OUTFITTING)).containsExactlyInAnyOrder(entity1);
    }
}
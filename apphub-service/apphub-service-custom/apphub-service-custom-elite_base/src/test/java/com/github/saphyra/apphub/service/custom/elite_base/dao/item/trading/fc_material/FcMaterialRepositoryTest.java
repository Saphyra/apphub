package com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.fc_material;

import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemEntityId;
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
class FcMaterialRepositoryTest {
    private static final String EXTERNAL_REFERENCE_1 = "external-reference-1";
    private static final String EXTERNAL_REFERENCE_2 = "external-reference-2";
    private static final String ITEM_NAME_1 = "item-name-1";
    private static final Long MARKET_ID_1 = 232L;
    private static final Long MARKET_ID_2 = 23234L;

    @Autowired
    private FcMaterialRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    void getByMarketId() {
        FcMaterialEntity entity1 = FcMaterialEntity.builder()
            .id(ItemEntityId.builder()
                .externalReference(EXTERNAL_REFERENCE_1)
                .itemName(ITEM_NAME_1)
                .build())
            .marketId(MARKET_ID_1)
            .build();
        underTest.save(entity1);
        FcMaterialEntity entity2 = FcMaterialEntity.builder()
            .id(ItemEntityId.builder()
                .externalReference(EXTERNAL_REFERENCE_2)
                .itemName(ITEM_NAME_1)
                .build())
            .marketId(MARKET_ID_2)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByMarketId(MARKET_ID_1)).containsExactly(entity1);
    }
}
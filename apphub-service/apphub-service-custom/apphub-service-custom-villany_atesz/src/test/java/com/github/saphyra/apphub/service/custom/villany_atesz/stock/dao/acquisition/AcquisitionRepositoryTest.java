package com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.acquisition;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
class AcquisitionRepositoryTest {
    private static final String ACQUISITION_ID_1 = "acquisition-id-1";
    private static final String ACQUISITION_ID_2 = "acquisition-id-2";
    private static final String ACQUISITION_ID_3 = "acquisition-id-3";
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";
    private static final String ACQUIRED_AT_1 = "acquired-at-1";
    private static final String ACQUIRED_AT_2 = "acquired-at-2";
    private static final String STOCK_ITEM_ID_1 = "stock-item-id-1";
    private static final String STOCK_ITEM_ID_2 = "stock-item-id-2";

    @Autowired
    private AcquisitionRepository underTest;

    @AfterEach
    void clear() {
        underTest.deleteAll();
    }

    @Transactional
    @Test
    void deleteByUserId() {
        AcquisitionEntity entity1 = AcquisitionEntity.builder()
            .acquisitionId(ACQUISITION_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);

        AcquisitionEntity entity2 = AcquisitionEntity.builder()
            .acquisitionId(ACQUISITION_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByUserId(USER_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    void getDistinctAcquiredAtByUserId() {
        AcquisitionEntity entity1 = AcquisitionEntity.builder()
            .acquisitionId(ACQUISITION_ID_1)
            .userId(USER_ID_1)
            .acquiredAt(ACQUIRED_AT_1)
            .build();
        underTest.save(entity1);

        AcquisitionEntity entity2 = AcquisitionEntity.builder()
            .acquisitionId(ACQUISITION_ID_2)
            .userId(USER_ID_1)
            .acquiredAt(ACQUIRED_AT_1)
            .build();
        underTest.save(entity2);

        AcquisitionEntity entity3 = AcquisitionEntity.builder()
            .acquisitionId(ACQUISITION_ID_3)
            .userId(USER_ID_2)
            .acquiredAt(ACQUIRED_AT_2)
            .build();
        underTest.save(entity3);

        assertThat(underTest.getDistinctAcquiredAtByUserId(USER_ID_1)).containsExactly(ACQUIRED_AT_1);
    }

    @Test
    void getByAcquiredAtAndUserId() {
        AcquisitionEntity entity1 = AcquisitionEntity.builder()
            .acquisitionId(ACQUISITION_ID_1)
            .userId(USER_ID_1)
            .acquiredAt(ACQUIRED_AT_1)
            .build();
        underTest.save(entity1);

        AcquisitionEntity entity2 = AcquisitionEntity.builder()
            .acquisitionId(ACQUISITION_ID_2)
            .userId(USER_ID_1)
            .acquiredAt(ACQUIRED_AT_2)
            .build();
        underTest.save(entity2);

        AcquisitionEntity entity3 = AcquisitionEntity.builder()
            .acquisitionId(ACQUISITION_ID_3)
            .userId(USER_ID_2)
            .acquiredAt(ACQUIRED_AT_1)
            .build();
        underTest.save(entity3);

        assertThat(underTest.getByAcquiredAtAndUserId(ACQUIRED_AT_1, USER_ID_1)).containsExactly(entity1);
    }

    @Transactional
    @Test
    void deleteByStockItemIdAndUserId() {
        AcquisitionEntity entity1 = AcquisitionEntity.builder()
            .acquisitionId(ACQUISITION_ID_1)
            .userId(USER_ID_1)
            .stockItemId(STOCK_ITEM_ID_1)
            .build();
        underTest.save(entity1);

        AcquisitionEntity entity2 = AcquisitionEntity.builder()
            .acquisitionId(ACQUISITION_ID_2)
            .userId(USER_ID_1)
            .stockItemId(STOCK_ITEM_ID_2)
            .build();
        underTest.save(entity2);

        AcquisitionEntity entity3 = AcquisitionEntity.builder()
            .acquisitionId(ACQUISITION_ID_3)
            .userId(USER_ID_2)
            .stockItemId(STOCK_ITEM_ID_1)
            .build();
        underTest.save(entity3);

        underTest.deleteByStockItemIdAndUserId(STOCK_ITEM_ID_1, USER_ID_1);

        assertThat(underTest.findAll()).containsExactlyInAnyOrder(entity2, entity3);
    }
}
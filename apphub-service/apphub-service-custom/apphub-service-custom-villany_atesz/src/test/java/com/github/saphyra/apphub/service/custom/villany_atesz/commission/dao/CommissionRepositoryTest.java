package com.github.saphyra.apphub.service.custom.villany_atesz.commission.dao;

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
class CommissionRepositoryTest {
    private static final String COMMISSION_ID_1 = "commission-id-1";
    private static final String COMMISSION_ID_2 = "commission-id-2";
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";

    @Autowired
    private CommissionRepository underTest;

    @AfterEach
    void clear() {
        underTest.deleteAll();
    }

    @Test
    void getByUserId() {
        CommissionEntity entity1 = CommissionEntity.builder()
            .commissionId(COMMISSION_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);

        CommissionEntity entity2 = CommissionEntity.builder()
            .commissionId(COMMISSION_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByUserId(USER_ID_1)).containsExactly(entity1);
    }

    @Test
    @Transactional
    void deleteByUserId() {
        CommissionEntity entity1 = CommissionEntity.builder()
            .commissionId(COMMISSION_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);

        CommissionEntity entity2 = CommissionEntity.builder()
            .commissionId(COMMISSION_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByUserId(USER_ID_2);

        assertThat(underTest.findAll()).containsExactly(entity1);
    }
}
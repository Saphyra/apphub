package com.github.saphyra.apphub.service.user.ban.dao;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
public class BanRepositoryTest {
    private static final String BAN_ID_1 = "ban-id-1";
    private static final String BAN_ID_2 = "ban-id-2";
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";
    private static final LocalDateTime CURRENT_DATE = LocalDateTime.now();

    @Autowired
    private BanRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    public void deleteByUserId() {
        BanEntity entity1 = BanEntity.builder()
            .id(BAN_ID_1)
            .userId(USER_ID_1)
            .build();
        BanEntity entity2 = BanEntity.builder()
            .id(BAN_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.saveAll(List.of(entity1, entity2));

        underTest.deleteByUserId(USER_ID_2);

        assertThat(underTest.findAll()).containsExactly(entity1);
    }

    @Test
    public void getByUserId() {
        BanEntity entity1 = BanEntity.builder()
            .id(BAN_ID_1)
            .userId(USER_ID_1)
            .build();
        BanEntity entity2 = BanEntity.builder()
            .id(BAN_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.saveAll(List.of(entity1, entity2));

        List<BanEntity> result = underTest.getByUserId(USER_ID_1);

        assertThat(result).containsExactly(entity1);
    }

    @Test
    @Transactional
    public void deleteExpired() {
        BanEntity entity1 = BanEntity.builder()
            .id(BAN_ID_1)
            .userId(USER_ID_1)
            .expiration(CURRENT_DATE.plusSeconds(1))
            .build();
        BanEntity entity2 = BanEntity.builder()
            .id(BAN_ID_2)
            .userId(USER_ID_2)
            .expiration(CURRENT_DATE.minusSeconds(1))
            .build();
        underTest.saveAll(List.of(entity1, entity2));

        underTest.deleteExpired(CURRENT_DATE);

        assertThat(underTest.findAll()).containsExactly(entity1);
    }
}
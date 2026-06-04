package com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao;

import com.github.saphyra.apphub.test.repository.RepositoryTestConfiguration;
import jakarta.transaction.Transactional;
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
@Deprecated(forRemoval = true)
class DeprecatedEventRepositoryTest {
    private static final String EVENT_ID_1 = "event-id-1";
    private static final String EVENT_ID_2 = "event-id-2";
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";

    @Autowired
    private DeprecatedEventRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    void deleteByUserId() {
        DeprecatedEventEntity entity1 = DeprecatedEventEntity.builder()
            .eventId(EVENT_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);
        DeprecatedEventEntity entity2 = DeprecatedEventEntity.builder()
            .eventId(EVENT_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByUserId(USER_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    void getByUserId() {
        DeprecatedEventEntity entity1 = DeprecatedEventEntity.builder()
            .eventId(EVENT_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);
        DeprecatedEventEntity entity2 = DeprecatedEventEntity.builder()
            .eventId(EVENT_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByUserId(USER_ID_1)).containsExactly(entity1);
    }

    @Test
    @Transactional
    void deleteByUserIdAndEventId() {
        DeprecatedEventEntity entity1 = DeprecatedEventEntity.builder()
            .eventId(EVENT_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);
        DeprecatedEventEntity entity2 = DeprecatedEventEntity.builder()
            .eventId(EVENT_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByUserIdAndEventId(USER_ID_1, EVENT_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }
}
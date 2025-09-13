package com.github.saphyra.apphub.service.calendar.domain.occurrence.dao;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
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
class OccurrenceRepositoryTest {
    private static final String ID_1 = "id-1";
    private static final String ID_2 = "id-2";
    private static final String ID_3 = "id-3";
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";
    private static final String EVENT_ID_1 = "event-id-1";
    private static final String EVENT_ID_2 = "event-id-2";

    @Autowired
    private OccurrenceRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    void deleteByUserId() {
        OccurrenceEntity entity1 = OccurrenceEntity.builder()
            .occurrenceId(ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);
        OccurrenceEntity entity2 = OccurrenceEntity.builder()
            .occurrenceId(ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByUserId(USER_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    @Transactional
    void deleteByUserIdAndEventId(){
        OccurrenceEntity entity1 = OccurrenceEntity.builder()
            .occurrenceId(ID_1)
            .userId(USER_ID_1)
            .eventId(EVENT_ID_1)
            .build();
        underTest.save(entity1);
        OccurrenceEntity entity2 = OccurrenceEntity.builder()
            .occurrenceId(ID_2)
            .userId(USER_ID_2)
            .eventId(EVENT_ID_1)
            .build();
        underTest.save(entity2);
        OccurrenceEntity entity3 = OccurrenceEntity.builder()
            .occurrenceId(ID_3)
            .userId(USER_ID_1)
            .eventId(EVENT_ID_2)
            .build();
        underTest.save(entity3);

        underTest.deleteByUserIdAndEventId(USER_ID_1, EVENT_ID_1);

        assertThat(underTest.findAll()).containsExactlyInAnyOrder(entity2, entity3);
    }

    @Test
    void getByEventId(){
        OccurrenceEntity entity1 = OccurrenceEntity.builder()
            .occurrenceId(ID_1)
            .eventId(EVENT_ID_1)
            .build();
        underTest.save(entity1);
        OccurrenceEntity entity2 = OccurrenceEntity.builder()
            .occurrenceId(ID_2)
            .eventId(EVENT_ID_2)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByEventId(EVENT_ID_1)).containsExactlyInAnyOrder(entity1);
    }

    @Test
    void getByUserId(){
        OccurrenceEntity entity1 = OccurrenceEntity.builder()
            .occurrenceId(ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);
        OccurrenceEntity entity2 = OccurrenceEntity.builder()
            .occurrenceId(ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByUserId(USER_ID_1)).containsExactlyInAnyOrder(entity1);
    }
}
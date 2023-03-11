package com.github.saphyra.apphub.service.calendar.dao.occurance;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
public class OccurrenceRepositoryTest {
    private static final String OCCURRENCE_ID_1 = "occurrence-id-1";
    private static final String OCCURRENCE_ID_2 = "occurrence-id-2";
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
    public void deleteByUserId() {
        OccurrenceEntity entity1 = OccurrenceEntity.builder()
            .occurrenceId(OCCURRENCE_ID_1)
            .userId(USER_ID_1)
            .build();
        OccurrenceEntity entity2 = OccurrenceEntity.builder()
            .occurrenceId(OCCURRENCE_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.saveAll(List.of(entity1, entity2));

        underTest.deleteByUserId(USER_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    public void getByUserId() {
        OccurrenceEntity entity1 = OccurrenceEntity.builder()
            .occurrenceId(OCCURRENCE_ID_1)
            .userId(USER_ID_1)
            .build();
        OccurrenceEntity entity2 = OccurrenceEntity.builder()
            .occurrenceId(OCCURRENCE_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.saveAll(List.of(entity1, entity2));

        List<OccurrenceEntity> result = underTest.getByUserId(USER_ID_1);

        assertThat(result).containsExactly(entity1);
    }

    @Test
    public void getByEventId() {
        OccurrenceEntity entity1 = OccurrenceEntity.builder()
            .occurrenceId(OCCURRENCE_ID_1)
            .eventId(EVENT_ID_1)
            .build();
        OccurrenceEntity entity2 = OccurrenceEntity.builder()
            .occurrenceId(OCCURRENCE_ID_2)
            .eventId(EVENT_ID_2)
            .build();
        underTest.saveAll(List.of(entity1, entity2));

        List<OccurrenceEntity> result = underTest.getByEventId(EVENT_ID_1);

        assertThat(result).containsExactly(entity1);
    }

    @Test
    @Transactional
    public void deleteByEventId() {
        OccurrenceEntity entity1 = OccurrenceEntity.builder()
            .occurrenceId(OCCURRENCE_ID_1)
            .eventId(EVENT_ID_1)
            .build();
        OccurrenceEntity entity2 = OccurrenceEntity.builder()
            .occurrenceId(OCCURRENCE_ID_2)
            .eventId(EVENT_ID_2)
            .build();
        underTest.saveAll(List.of(entity1, entity2));

        underTest.deleteByEventId(EVENT_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }
}
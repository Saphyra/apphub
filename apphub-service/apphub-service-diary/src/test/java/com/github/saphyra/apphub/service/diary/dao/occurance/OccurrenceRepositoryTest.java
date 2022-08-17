package com.github.saphyra.apphub.service.diary.dao.occurance;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
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

    @After
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
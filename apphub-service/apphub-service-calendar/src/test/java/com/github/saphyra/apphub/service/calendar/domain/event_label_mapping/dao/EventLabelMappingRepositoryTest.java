package com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.dao;

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
class EventLabelMappingRepositoryTest {
    private static final String LABEL_ID_1 = "label-id-1";
    private static final String USER_ID_1 = "user-id-1";
    private static final String EVENT_ID_1 = "event-id-1";
    private static final String LABEL_ID_2 = "label-id-2";
    private static final String USER_ID_2 = "user-id-2";
    private static final String EVENT_ID_2 = "event-id-2";

    @Autowired
    private EventLabelMappingRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    void deleteByUserId(){
        EventLabelMappingEntity entity1 = EventLabelMappingEntity.builder()
            .labelId(LABEL_ID_1)
            .userId(USER_ID_1)
            .eventId(EVENT_ID_1)
            .build();
        underTest.save(entity1);
        EventLabelMappingEntity entity2 = EventLabelMappingEntity.builder()
            .labelId(LABEL_ID_1)
            .userId(USER_ID_2)
            .eventId(EVENT_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByUserId(USER_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    @Transactional
    void deleteByUserIdAndEventId(){
        EventLabelMappingEntity entity1 = EventLabelMappingEntity.builder()
            .labelId(LABEL_ID_1)
            .userId(USER_ID_1)
            .eventId(EVENT_ID_1)
            .build();
        underTest.save(entity1);
        EventLabelMappingEntity entity2 = EventLabelMappingEntity.builder()
            .labelId(LABEL_ID_1)
            .userId(USER_ID_1)
            .eventId(EVENT_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByUserIdAndEventId(USER_ID_1, EVENT_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    void getByEventId(){
        EventLabelMappingEntity entity1 = EventLabelMappingEntity.builder()
            .labelId(LABEL_ID_1)
            .userId(USER_ID_1)
            .eventId(EVENT_ID_1)
            .build();
        underTest.save(entity1);
        EventLabelMappingEntity entity2 = EventLabelMappingEntity.builder()
            .labelId(LABEL_ID_2)
            .userId(USER_ID_2)
            .eventId(EVENT_ID_2)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByEventId(EVENT_ID_1)).containsExactly(entity1);
    }

    @Test
    @Transactional
    void deleteByUserIdAndLabelId(){
        EventLabelMappingEntity entity1 = EventLabelMappingEntity.builder()
            .labelId(LABEL_ID_1)
            .userId(USER_ID_1)
            .eventId(EVENT_ID_1)
            .build();
        underTest.save(entity1);
        EventLabelMappingEntity entity2 = EventLabelMappingEntity.builder()
            .labelId(LABEL_ID_2)
            .userId(USER_ID_1)
            .eventId(EVENT_ID_2)
            .build();
        underTest.save(entity2);
        EventLabelMappingEntity entity3 = EventLabelMappingEntity.builder()
            .labelId(LABEL_ID_1)
            .userId(USER_ID_2)
            .eventId(EVENT_ID_2)
            .build();
        underTest.save(entity3);

        underTest.deleteByUserIdAndLabelId(USER_ID_1, LABEL_ID_1);

        assertThat(underTest.findAll()).containsExactlyInAnyOrder(entity2, entity3);
    }

    @Test
    void getByUserIdAndLabelId(){
        EventLabelMappingEntity entity1 = EventLabelMappingEntity.builder()
            .labelId(LABEL_ID_1)
            .userId(USER_ID_1)
            .eventId(EVENT_ID_1)
            .build();
        underTest.save(entity1);
        EventLabelMappingEntity entity2 = EventLabelMappingEntity.builder()
            .labelId(LABEL_ID_1)
            .userId(USER_ID_2)
            .eventId(EVENT_ID_2)
            .build();
        underTest.save(entity2);
        EventLabelMappingEntity entity3 = EventLabelMappingEntity.builder()
            .labelId(LABEL_ID_2)
            .userId(USER_ID_1)
            .eventId(EVENT_ID_2)
            .build();
        underTest.save(entity3);

        assertThat(underTest.getByUserIdAndLabelId(USER_ID_1, LABEL_ID_1)).containsExactly(entity1);
    }
}
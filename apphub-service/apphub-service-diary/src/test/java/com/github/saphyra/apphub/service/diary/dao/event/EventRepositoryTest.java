package com.github.saphyra.apphub.service.diary.dao.event;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import org.junit.After;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
public class EventRepositoryTest {
    private static final String EVENT_ID_1 = "event-id-1";
    private static final String EVENT_ID_2 = "event-id-2";
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";

    @Autowired
    private EventRepository underTest;

    @After
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    public void deleteByUserId() {
        EventEntity entity1 = EventEntity.builder()
            .eventId(EVENT_ID_1)
            .userId(USER_ID_1)
            .build();
        EventEntity entity2 = EventEntity.builder()
            .eventId(EVENT_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.saveAll(List.of(entity1, entity2));

        underTest.deleteByUserId(USER_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    public void getByUserId() {
        EventEntity entity1 = EventEntity.builder()
            .eventId(EVENT_ID_1)
            .userId(USER_ID_1)
            .build();
        EventEntity entity2 = EventEntity.builder()
            .eventId(EVENT_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.saveAll(List.of(entity1, entity2));

        List<EventEntity> result = underTest.getByUserId(USER_ID_1);

        assertThat(result).containsExactly(entity1);
    }
}
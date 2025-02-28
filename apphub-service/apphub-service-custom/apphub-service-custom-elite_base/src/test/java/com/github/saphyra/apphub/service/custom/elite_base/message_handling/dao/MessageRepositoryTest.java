package com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@Slf4j
class MessageRepositoryTest {
    private static final String MESSAGE_ID_1 = "message-id-1";
    private static final String MESSAGE_ID_2 = "message-id-2";
    private static final String MESSAGE_ID_3 = "message-id-3";
    private static final String MESSAGE_ID_4 = "message-id-4";
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();

    @Autowired
    private MessageRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    void resetUnhandled() {
        MessageEntity entity1 = MessageEntity.builder()
            .messageId(MESSAGE_ID_1)
            .status(MessageStatus.PROCESSED)
            .build();
        underTest.save(entity1);

        MessageEntity entity2 = MessageEntity.builder()
            .messageId(MESSAGE_ID_2)
            .status(MessageStatus.UNHANDLED)
            .build();
        underTest.save(entity2);

        underTest.setStatus(MessageStatus.UNHANDLED, MessageStatus.ARRIVED);

        assertThat(underTest.findById(MESSAGE_ID_1).get().getStatus()).isEqualTo(MessageStatus.PROCESSED);
        assertThat(underTest.findById(MESSAGE_ID_2).get().getStatus()).isEqualTo(MessageStatus.ARRIVED);
    }

    @Test
    void deleteByCreatedAtBeforeAndStatusIn() {
        MessageEntity entity1 = MessageEntity.builder()
            .messageId(MESSAGE_ID_1)
            .createdAt(CURRENT_TIME.minusSeconds(1).toString())
            .status(MessageStatus.ARRIVED)
            .build();
        underTest.save(entity1);

        MessageEntity entity2 = MessageEntity.builder()
            .messageId(MESSAGE_ID_2)
            .createdAt(CURRENT_TIME.toString())
            .build();
        underTest.save(entity2);

        MessageEntity entity3 = MessageEntity.builder()
            .messageId(MESSAGE_ID_3)
            .createdAt(CURRENT_TIME.minusSeconds(1).toString())
            .status(MessageStatus.PROCESSED)
            .build();
        underTest.save(entity3);

        underTest.deleteByCreatedAtBeforeAndStatusIn(CURRENT_TIME.toString(), List.of(MessageStatus.ARRIVED));

        assertThat(underTest.findAll()).containsExactlyInAnyOrder(entity2, entity3);
    }

    @Test
    void findByCreatedAtBeforeAndStatusOrderByCreatedAtAsc() {
        MessageEntity entity1 = MessageEntity.builder()
            .messageId(MESSAGE_ID_1)
            .createdAt(CURRENT_TIME.minusMinutes(1).toString())
            .status(MessageStatus.PROCESSED)
            .build();
        underTest.save(entity1);

        MessageEntity entity2 = MessageEntity.builder()
            .messageId(MESSAGE_ID_2)
            .createdAt(CURRENT_TIME.minusSeconds(3).toString())
            .status(MessageStatus.ARRIVED)
            .build();
        underTest.save(entity2);

        MessageEntity entity3 = MessageEntity.builder()
            .messageId(MESSAGE_ID_3)
            .createdAt(CURRENT_TIME.minusSeconds(2).toString())
            .status(MessageStatus.ARRIVED)
            .build();
        underTest.save(entity3);

        MessageEntity entity4 = MessageEntity.builder()
            .messageId(MESSAGE_ID_4)
            .createdAt(CURRENT_TIME.minusSeconds(1).toString())
            .status(MessageStatus.ARRIVED)
            .build();
        underTest.save(entity4);

        assertThat(underTest.getByCreatedAtBeforeAndStatusOrderByCreatedAtAsc(CURRENT_TIME.toString(), MessageStatus.ARRIVED, PageRequest.of(0, 2))).containsExactly(entity2, entity3);
        assertThat(underTest.getByCreatedAtBeforeAndStatusOrderByCreatedAtAsc(CURRENT_TIME.toString(), MessageStatus.ARRIVED, PageRequest.of(1, 2))).containsExactly(entity4);
    }

    @Test
    void findByCreatedAtBeforeAndStatusOrderByCreatedAtAsc_checkTimeLimit() {
        MessageEntity entity1 = MessageEntity.builder()
            .messageId(MESSAGE_ID_1)
            .createdAt(CURRENT_TIME.minusMinutes(1).toString())
            .status(MessageStatus.PROCESSED)
            .build();
        underTest.save(entity1);

        MessageEntity entity2 = MessageEntity.builder()
            .messageId(MESSAGE_ID_2)
            .createdAt(CURRENT_TIME.minusSeconds(3).toString())
            .status(MessageStatus.ARRIVED)
            .build();
        underTest.save(entity2);

        MessageEntity entity3 = MessageEntity.builder()
            .messageId(MESSAGE_ID_3)
            .createdAt(CURRENT_TIME.minusSeconds(2).toString())
            .status(MessageStatus.ARRIVED)
            .build();
        underTest.save(entity3);

        MessageEntity entity4 = MessageEntity.builder()
            .messageId(MESSAGE_ID_4)
            .createdAt(CURRENT_TIME.minusSeconds(1).toString())
            .status(MessageStatus.ARRIVED)
            .build();
        underTest.save(entity4);

        assertThat(underTest.getByCreatedAtBeforeAndStatusOrderByCreatedAtAsc(CURRENT_TIME.minusSeconds(2).toString(), MessageStatus.ARRIVED, PageRequest.of(0, 2))).containsExactly(entity2);
    }
}
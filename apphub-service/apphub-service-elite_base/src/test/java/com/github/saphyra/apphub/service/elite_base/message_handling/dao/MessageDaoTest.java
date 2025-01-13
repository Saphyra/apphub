package com.github.saphyra.apphub.service.elite_base.message_handling.dao;

import com.github.saphyra.apphub.lib.common_util.DateTimeConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class MessageDaoTest {
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    private static final String CURRENT_TIME_STRING = "current-time";
    private static final Integer BATCH_SIZE = 23;

    @Mock
    private DateTimeConverter dateTimeConverter;

    @Mock
    private MessageConverter converter;

    @Mock
    private MessageRepository repository;

    @InjectMocks
    private MessageDao underTest;

    @Mock
    private MessageEntity entity;

    @Mock
    private EdMessage domain;

    @Test
    void resetUnhandled() {
        underTest.resetUnhandled();

        then(repository).should().resetUnhandled(MessageStatus.UNHANDLED, MessageStatus.ARRIVED);
    }

    @Test
    void deleteExpired() {
        given(dateTimeConverter.convertDomain(CURRENT_TIME)).willReturn(CURRENT_TIME_STRING);

        underTest.deleteExpired(CURRENT_TIME, List.of(MessageStatus.PROCESSED));

        then(repository).should().deleteByCreatedAtBeforeAndStatusIn(CURRENT_TIME_STRING, List.of(MessageStatus.PROCESSED));
    }

    @Test
    void getMessages() {
        given(dateTimeConverter.convertDomain(CURRENT_TIME)).willReturn(CURRENT_TIME_STRING);
        given(repository.getByCreatedAtBeforeAndStatusOrderByCreatedAtAsc(CURRENT_TIME_STRING, MessageStatus.ARRIVED, PageRequest.of(0, BATCH_SIZE))).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.getMessages(CURRENT_TIME, BATCH_SIZE)).containsExactly(domain);
    }
}
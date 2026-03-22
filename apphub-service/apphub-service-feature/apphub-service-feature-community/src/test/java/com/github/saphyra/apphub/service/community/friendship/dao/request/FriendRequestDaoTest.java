package com.github.saphyra.apphub.service.community.friendship.dao.request;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class FriendRequestDaoTest {
    private static final UUID SENDER_ID = UUID.randomUUID();
    private static final UUID RECEIVER_ID = UUID.randomUUID();
    private static final String SENDER_ID_STRING = "sender-id";
    private static final String RECEIVER_ID_STRING = "receiver-id";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final UUID FRIEND_REQUEST_ID = UUID.randomUUID();
    private static final String FRIEND_REQUEST_ID_STRING = "friend-request-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private FriendRequestConverter converter;

    @Mock
    private FriendRequestRepository repository;

    @InjectMocks
    private FriendRequestDao underTest;

    @Mock
    private FriendRequest domain;

    @Mock
    private FriendRequestEntity entity;

    @Test
    public void deleteBySenderIdAndReceiverId() {
        given(uuidConverter.convertDomain(SENDER_ID)).willReturn(SENDER_ID_STRING);
        given(uuidConverter.convertDomain(RECEIVER_ID)).willReturn(RECEIVER_ID_STRING);

        underTest.deleteBySenderIdAndReceiverId(SENDER_ID, RECEIVER_ID);

        verify(repository).deleteBySenderIdAndReceiverId(SENDER_ID_STRING, RECEIVER_ID_STRING);
    }

    @Test
    public void getBySenderIdOrReceiverId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getBySenderIdOrReceiverId(USER_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        List<FriendRequest> result = underTest.getBySenderIdOrReceiverId(USER_ID);

        assertThat(result).containsExactly(domain);
    }

    @Test
    public void getBySenderId() {
        given(uuidConverter.convertDomain(SENDER_ID)).willReturn(SENDER_ID_STRING);
        given(repository.getBySenderId(SENDER_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        List<FriendRequest> result = underTest.getBySenderId(SENDER_ID);

        assertThat(result).containsExactly(domain);
    }

    @Test
    public void getByReceiverId() {
        given(uuidConverter.convertDomain(RECEIVER_ID)).willReturn(RECEIVER_ID_STRING);
        given(repository.getByReceiverId(RECEIVER_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        List<FriendRequest> result = underTest.getByReceiverId(RECEIVER_ID);

        assertThat(result).containsExactly(domain);
    }

    @Test
    public void findBySenderIdAndReceiverId() {
        given(uuidConverter.convertDomain(SENDER_ID)).willReturn(SENDER_ID_STRING);
        given(uuidConverter.convertDomain(RECEIVER_ID)).willReturn(RECEIVER_ID_STRING);
        given(repository.findBySenderIdAndReceiverId(SENDER_ID_STRING, RECEIVER_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        Optional<FriendRequest> result = underTest.findBySenderIdAndReceiverId(SENDER_ID, RECEIVER_ID);

        assertThat(result).contains(domain);
    }

    @Test
    public void findById() {
        given(uuidConverter.convertDomain(FRIEND_REQUEST_ID)).willReturn(FRIEND_REQUEST_ID_STRING);
        given(repository.findById(FRIEND_REQUEST_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        Optional<FriendRequest> result = underTest.findById(FRIEND_REQUEST_ID);

        assertThat(result).contains(domain);
    }

    @Test
    public void deleteByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        underTest.deleteByUserId(USER_ID);

        verify(repository).deleteByUserId(USER_ID_STRING);
    }
}
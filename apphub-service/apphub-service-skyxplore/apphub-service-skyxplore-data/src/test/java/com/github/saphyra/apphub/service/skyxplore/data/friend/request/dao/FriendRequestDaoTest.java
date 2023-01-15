package com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class FriendRequestDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final UUID FRIEND_ID = UUID.randomUUID();
    private static final String FRIEND_ID_STRING = "friend-id";

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

    @BeforeEach
    public void setUp() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(FRIEND_ID)).willReturn(FRIEND_ID_STRING);
    }

    @Test
    public void deleteByUserId() {
        underTest.deleteByUserId(USER_ID);

        verify(repository).deleteBySenderIdOrFriendId(USER_ID_STRING);
    }

    @Test
    public void findBySenderIdAndFriendId() {
        given(repository.findBySenderIdAndFriendId(USER_ID_STRING, FRIEND_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        Optional<FriendRequest> result = underTest.findBySenderIdAndFriendId(USER_ID, FRIEND_ID);

        assertThat(result).contains(domain);
    }

    @Test
    public void getBySenderId() {
        given(repository.getBySenderId(USER_ID_STRING)).willReturn(Arrays.asList(entity));
        given(converter.convertEntity(Arrays.asList(entity))).willReturn(Arrays.asList(domain));

        List<FriendRequest> result = underTest.getBySenderId(USER_ID);

        assertThat(result).containsExactly(domain);
    }

    @Test
    public void findById() {
        given(repository.findById(USER_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        Optional<FriendRequest> result = underTest.findById(USER_ID);

        assertThat(result).contains(domain);
    }

    @Test
    public void getByFriendId() {
        given(repository.getByFriendId(USER_ID_STRING)).willReturn(Arrays.asList(entity));
        given(converter.convertEntity(Arrays.asList(entity))).willReturn(Arrays.asList(domain));

        List<FriendRequest> result = underTest.getByFriendId(USER_ID);

        assertThat(result).containsExactly(domain);
    }
}
package com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FriendshipDaoTest {
    private static final String USER_ID_STRING = "user-id";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID FRIEND_1 = UUID.randomUUID();
    private static final UUID FRIEND_2 = UUID.randomUUID();
    private static final String FRIEND_1_STRING = "friend-1";
    private static final String FRIEND_2_STRING = "friend-2";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private FriendshipConverter converter;

    @Mock
    private FriendshipRepository repository;

    @InjectMocks
    private FriendshipDao underTest;

    @Mock
    private FriendshipEntity entity;

    @Mock
    private Friendship domain;

    @Before
    public void setUp() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
    }

    @Test
    public void getByFriendId() {
        given(repository.getByFriendId(USER_ID_STRING)).willReturn(Arrays.asList(entity));
        given(converter.convertEntity(Arrays.asList(entity))).willReturn(Arrays.asList(domain));

        List<Friendship> result = underTest.getByFriendId(USER_ID);

        assertThat(result).containsExactly(domain);
    }

    @Test
    public void findById() {
        given(repository.findById(USER_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        Optional<Friendship> result = underTest.findById(USER_ID);

        assertThat(result).contains(domain);
    }

    @Test
    public void deleteByUserId() {
        underTest.deleteByUserId(USER_ID);

        verify(repository).deleteByFriendId(USER_ID_STRING);
    }

    @Test
    public void findByFriendIds() {
        given(uuidConverter.convertDomain(FRIEND_1)).willReturn(FRIEND_1_STRING);
        given(uuidConverter.convertDomain(FRIEND_2)).willReturn(FRIEND_2_STRING);
        given(repository.findByFriendIds(FRIEND_1_STRING, FRIEND_2_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        Optional<Friendship> result = underTest.findByFriendIds(FRIEND_1, FRIEND_2);

        assertThat(result).contains(domain);
    }
}
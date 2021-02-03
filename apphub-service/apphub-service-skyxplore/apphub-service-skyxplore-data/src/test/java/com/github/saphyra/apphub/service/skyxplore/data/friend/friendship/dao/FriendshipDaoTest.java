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
}
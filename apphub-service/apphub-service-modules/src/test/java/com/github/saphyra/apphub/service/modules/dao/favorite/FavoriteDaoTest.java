package com.github.saphyra.apphub.service.modules.dao.favorite;

import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class FavoriteDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";

    @Mock
    private FavoriteConverter converter;

    @Mock
    private FavoriteRepository repository;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private FavoriteDao underTest;

    @Mock
    private FavoriteEntity entity;

    @Mock
    private Favorite favorite;

    @Test
    public void getByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByUserId(USER_ID_STRING)).willReturn(Arrays.asList(entity));
        given(converter.convertEntity(Arrays.asList(entity))).willReturn(Arrays.asList(favorite));

        List<Favorite> result = underTest.getByUserId(USER_ID);

        assertThat(result).containsExactly(favorite);
    }

    @Test
    public void deleteByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        underTest.deleteByUserId(USER_ID);

        verify(repository).deleteByUserId(USER_ID_STRING);
    }
}
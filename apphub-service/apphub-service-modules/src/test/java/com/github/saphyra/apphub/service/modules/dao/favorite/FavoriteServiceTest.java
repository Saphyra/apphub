package com.github.saphyra.apphub.service.modules.dao.favorite;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class FavoriteServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String MODULE = "module";
    private static final String USER_ID_STRING = "user-id-string";

    @Mock
    private FavoriteDao favoriteDao;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private FavoriteService underTest;

    @Mock
    private Favorite favorite;

    @Test
    public void getByUserId() {
        given(favoriteDao.getByUserId(USER_ID)).willReturn(Arrays.asList(favorite));

        List<Favorite> result = underTest.getByUserId(USER_ID);

        assertThat(result).containsExactly(favorite);
    }

    @Test
    public void getOrDefault_found() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(favoriteDao.findById(any())).willReturn(Optional.of(favorite));

        Favorite result = underTest.getOrDefault(USER_ID, MODULE);

        ArgumentCaptor<FavoriteEntityKey> argumentCaptor = ArgumentCaptor.forClass(FavoriteEntityKey.class);
        verify(favoriteDao).findById(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getUserId()).isEqualTo(USER_ID_STRING);
        assertThat(argumentCaptor.getValue().getModule()).isEqualTo(MODULE);
        assertThat(result).isEqualTo(favorite);
    }

    @Test
    public void getOrDefault_default() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(favoriteDao.findById(any())).willReturn(Optional.empty());

        Favorite result = underTest.getOrDefault(USER_ID, MODULE);

        ArgumentCaptor<FavoriteEntityKey> argumentCaptor = ArgumentCaptor.forClass(FavoriteEntityKey.class);
        verify(favoriteDao).findById(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getUserId()).isEqualTo(USER_ID_STRING);
        assertThat(argumentCaptor.getValue().getModule()).isEqualTo(MODULE);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getModule()).isEqualTo(MODULE);
        assertThat(result.isFavorite()).isFalse();
    }

    @Test
    public void save() {
        underTest.save(favorite);

        verify(favoriteDao).save(favorite);
    }

    @Test
    public void deleteByUserId() {
        underTest.deleteByUserId(USER_ID);

        verify(favoriteDao).deleteByUserId(USER_ID);
    }
}
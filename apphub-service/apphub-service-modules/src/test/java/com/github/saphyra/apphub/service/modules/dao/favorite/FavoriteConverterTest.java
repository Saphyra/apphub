package com.github.saphyra.apphub.service.modules.dao.favorite;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.encryption.impl.BooleanEncryptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class FavoriteConverterTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String MODULE = "module";
    private static final String USER_ID_STRING = "user-id";
    private static final UUID ACCESS_TOKEN_USER_ID = UUID.randomUUID();
    private static final AccessTokenHeader ACCESS_TOKEN_HEADER = AccessTokenHeader.builder()
        .userId(ACCESS_TOKEN_USER_ID)
        .build();
    private static final String ACCESS_TOKEN_USER_ID_STRING = "access-token-user-id";
    private static final String ENCRYPTED_FAVORITE = "encrypted-favorite";

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private BooleanEncryptor booleanEncryptor;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private FavoriteConverter underTest;

    @Test
    public void convertEntity() {
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(accessTokenProvider.get()).willReturn(ACCESS_TOKEN_HEADER);
        given(uuidConverter.convertDomain(ACCESS_TOKEN_USER_ID)).willReturn(ACCESS_TOKEN_USER_ID_STRING);
        given(booleanEncryptor.decryptEntity(ENCRYPTED_FAVORITE, ACCESS_TOKEN_USER_ID_STRING)).willReturn(true);

        FavoriteEntity favorite = FavoriteEntity.builder()
            .key(
                FavoriteEntityKey.builder()
                    .userId(USER_ID_STRING)
                    .module(MODULE)
                    .build()
            )
            .favorite(ENCRYPTED_FAVORITE)
            .build();

        Favorite result = underTest.convertEntity(favorite);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getModule()).isEqualTo(MODULE);
        assertThat(result.isFavorite()).isTrue();
    }

    @Test
    public void convertDomain() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(accessTokenProvider.get()).willReturn(ACCESS_TOKEN_HEADER);
        given(uuidConverter.convertDomain(ACCESS_TOKEN_USER_ID)).willReturn(ACCESS_TOKEN_USER_ID_STRING);
        given(booleanEncryptor.encryptEntity(true, ACCESS_TOKEN_USER_ID_STRING)).willReturn(ENCRYPTED_FAVORITE);

        Favorite favorite = Favorite.builder()
            .userId(USER_ID)
            .module(MODULE)
            .favorite(true)
            .build();

        FavoriteEntity result = underTest.convertDomain(favorite);

        assertThat(result.getKey().getUserId()).isEqualTo(USER_ID_STRING);
        assertThat(result.getKey().getModule()).isEqualTo(MODULE);
        assertThat(result.getFavorite()).isEqualTo(ENCRYPTED_FAVORITE);
    }
}
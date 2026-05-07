package com.github.saphrya.apphub.service.platform.authorization.dao.refresh_token;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class RefreshTokenDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID REFRESH_TOKEN_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = USER_ID.toString();
    private static final String REFRESH_TOKEN_ID_STRING = REFRESH_TOKEN_ID.toString();

    @Mock
    private RefreshTokenRepository repository;

    @Mock
    private RefreshTokenConverter converter;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private RefreshTokenDao underTest;

    @Mock
    private RefreshToken refreshToken;

    @Mock
    private RefreshTokenEntity refreshTokenEntity;

    @Test
    void save() {
        given(converter.convertDomain(refreshToken)).willReturn(refreshTokenEntity);

        underTest.save(refreshToken);

        then(repository).should().save(refreshTokenEntity);
    }

    @Test
    void delete() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(REFRESH_TOKEN_ID)).willReturn(REFRESH_TOKEN_ID_STRING);

        underTest.delete(USER_ID, REFRESH_TOKEN_ID);

        then(repository).should().delete(USER_ID_STRING, REFRESH_TOKEN_ID_STRING);
    }

    @Test
    void findByUserIdAndRefreshTokenId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(REFRESH_TOKEN_ID)).willReturn(REFRESH_TOKEN_ID_STRING);
        given(repository.findById(USER_ID_STRING, REFRESH_TOKEN_ID_STRING)).willReturn(Optional.of(refreshTokenEntity));
        given(converter.convertEntity(Optional.of(refreshTokenEntity))).willReturn(Optional.of(refreshToken));

        Optional<RefreshToken> result = underTest.findByUserIdAndRefreshTokenId(USER_ID, REFRESH_TOKEN_ID);

        assertThat(result).contains(refreshToken);
    }

    @Test
    void deleteByUserId() {
        // Build 26 entities to force two batches (batch size = 25)
        List<RefreshTokenEntity> entities = new ArrayList<>();
        List<UUID> expectedIds = new ArrayList<>();

        for (int i = 0; i < 26; i++) {
            UUID tokenId = UUID.randomUUID();
            String tokenIdString = tokenId.toString();
            RefreshTokenEntity entity = RefreshTokenEntity.builder()
                .userId(USER_ID_STRING)
                .refreshTokenId(tokenIdString)
                .build();
            entities.add(entity);
            expectedIds.add(tokenId);
            given(uuidConverter.convertEntity(tokenIdString)).willReturn(tokenId);
        }

        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByUserId(USER_ID_STRING)).willReturn(entities);

        List<UUID> result = underTest.deleteByUserId(USER_ID);

        then(repository).should().delete(entities.subList(0, 25));
        then(repository).should().delete(entities.subList(25, 26));
        assertThat(result).containsExactlyInAnyOrderElementsOf(expectedIds);
    }

    @Test
    void getByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByUserId(USER_ID_STRING)).willReturn(List.of(refreshTokenEntity));
        given(converter.convertEntity(List.of(refreshTokenEntity))).willReturn(List.of(refreshToken));

        List<RefreshToken> result = underTest.getByUserId(USER_ID);

        assertThat(result).containsExactly(refreshToken);
    }
}
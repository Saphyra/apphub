package com.github.saphyra.apphub.service.modules.dao.favorite;

import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.converter.ConverterBase;
import com.github.saphyra.encryption.impl.BooleanEncryptor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
class FavoriteConverter extends ConverterBase<FavoriteEntity, Favorite> {
    private final AccessTokenProvider accessTokenProvider;
    private final BooleanEncryptor booleanEncryptor;
    private final UuidConverter uuidConverter;

    @Override
    protected Favorite processEntityConversion(FavoriteEntity favoriteEntity) {
        return Favorite.builder()
            .userId(uuidConverter.convertEntity(favoriteEntity.getKey().getUserId()))
            .module(favoriteEntity.getKey().getModule())
            .favorite(booleanEncryptor.decryptEntity(favoriteEntity.getFavorite(), uuidConverter.convertDomain(accessTokenProvider.get().getUserId())))
            .build();
    }

    @Override
    protected FavoriteEntity processDomainConversion(Favorite favorite) {
        return FavoriteEntity.builder()
            .key(
                FavoriteEntityKey.builder()
                    .userId(uuidConverter.convertDomain(favorite.getUserId()))
                    .module(favorite.getModule())
                    .build()
            )
            .favorite(booleanEncryptor.encryptEntity(favorite.isFavorite(), uuidConverter.convertDomain(accessTokenProvider.get().getUserId())))
            .build();
    }
}

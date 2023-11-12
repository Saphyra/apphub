package com.github.saphyra.apphub.service.modules.dao.favorite;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.BooleanEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
class FavoriteConverter extends ConverterBase<FavoriteEntity, Favorite> {
    static final String COLUMN_FAVORITE = "favorite";

    private final AccessTokenProvider accessTokenProvider;
    private final BooleanEncryptor booleanEncryptor;
    private final UuidConverter uuidConverter;

    @Override
    protected Favorite processEntityConversion(FavoriteEntity entity) {
        UUID userId = uuidConverter.convertEntity(entity.getKey().getUserId());
        return Favorite.builder()
            .userId(userId)
            .module(entity.getKey().getModule())
            .favorite(booleanEncryptor.decrypt(entity.getFavorite(), uuidConverter.convertDomain(accessTokenProvider.get().getUserId()), entity.getKey().getModule(), COLUMN_FAVORITE))
            .build();
    }

    @Override
    protected FavoriteEntity processDomainConversion(Favorite favorite) {
        String userId = uuidConverter.convertDomain(favorite.getUserId());
        return FavoriteEntity.builder()
            .key(
                FavoriteEntityKey.builder()
                    .userId(userId)
                    .module(favorite.getModule())
                    .build()
            )
            .favorite(booleanEncryptor.encrypt(favorite.isFavorite(), uuidConverter.convertDomain(accessTokenProvider.get().getUserId()), favorite.getModule(), COLUMN_FAVORITE))
            .build();
    }
}

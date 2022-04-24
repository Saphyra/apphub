package com.github.saphyra.apphub.service.community.friendship.dao.friend;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class FriendshipConverter extends ConverterBase<FriendshipEntity, Friendship> {
    private final UuidConverter uuidConverter;

    @Override
    protected Friendship processEntityConversion(FriendshipEntity entity) {
        return Friendship.builder()
            .friendshipId(uuidConverter.convertEntity(entity.getFriendshipId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .friendId(uuidConverter.convertEntity(entity.getFriendId()))
            .build();
    }

    @Override
    protected FriendshipEntity processDomainConversion(Friendship domain) {
        return FriendshipEntity.builder()
            .friendshipId(uuidConverter.convertDomain(domain.getFriendshipId()))
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .friendId(uuidConverter.convertDomain(domain.getFriendId()))
            .build();
    }
}

package com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class FriendshipConverter extends ConverterBase<FriendshipEntity, Friendship> {
    private final UuidConverter uuidConverter;

    @Override
    protected Friendship processEntityConversion(FriendshipEntity entity) {
        return Friendship.builder()
            .friendshipId(uuidConverter.convertEntity(entity.getFriendshipId()))
            .friend1(uuidConverter.convertEntity(entity.getFriend1()))
            .friend2(uuidConverter.convertEntity(entity.getFriend2()))
            .build();
    }

    @Override
    protected FriendshipEntity processDomainConversion(Friendship domain) {
        return FriendshipEntity.builder()
            .friendshipId(uuidConverter.convertDomain(domain.getFriendshipId()))
            .friend1(uuidConverter.convertDomain(domain.getFriend1()))
            .friend2(uuidConverter.convertDomain(domain.getFriend2()))
            .build();
    }
}

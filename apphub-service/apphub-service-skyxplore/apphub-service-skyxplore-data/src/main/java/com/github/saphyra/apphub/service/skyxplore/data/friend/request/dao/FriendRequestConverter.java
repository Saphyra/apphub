package com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class FriendRequestConverter extends ConverterBase<FriendRequestEntity, FriendRequest> {
    private final UuidConverter uuidConverter;

    @Override
    protected FriendRequest processEntityConversion(FriendRequestEntity entity) {
        return FriendRequest.builder()
            .friendRequestId(uuidConverter.convertEntity(entity.getFriendRequestId()))
            .senderId(uuidConverter.convertEntity(entity.getSenderId()))
            .friendId(uuidConverter.convertEntity(entity.getFriendId()))
            .build();
    }

    @Override
    protected FriendRequestEntity processDomainConversion(FriendRequest domain) {
        return FriendRequestEntity.builder()
            .friendRequestId(uuidConverter.convertDomain(domain.getFriendRequestId()))
            .senderId(uuidConverter.convertDomain(domain.getSenderId()))
            .friendId(uuidConverter.convertDomain(domain.getFriendId()))
            .build();
    }
}

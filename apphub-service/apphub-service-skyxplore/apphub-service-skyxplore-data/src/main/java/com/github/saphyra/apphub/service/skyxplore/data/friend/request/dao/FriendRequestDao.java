package com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
//TODO unit test
public class FriendRequestDao extends AbstractDao<FriendRequestEntity, FriendRequest, String, FriendRequestRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    public FriendRequestDao(FriendRequestConverter converter, FriendRequestRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteBySenderIdOrFriendId(uuidConverter.convertDomain(userId));
    }

    public Optional<FriendRequest> findBySenderIdAndFriendId(UUID senderId, UUID friendId) {
        return converter.convertEntity(
            repository.findBySenderIdAndFriendId(
                uuidConverter.convertDomain(senderId),
                uuidConverter.convertDomain(friendId)
            )
        );
    }

    public List<FriendRequest> getBySenderId(UUID userId) {
        return converter.convertEntity(repository.getBySenderId(uuidConverter.convertDomain(userId)));
    }

    public Optional<FriendRequest> findById(UUID friendRequestId) {
        return converter.convertEntity(repository.findById(uuidConverter.convertDomain(friendRequestId)));
    }
}

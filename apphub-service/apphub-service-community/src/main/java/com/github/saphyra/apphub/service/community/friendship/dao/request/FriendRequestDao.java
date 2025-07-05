package com.github.saphyra.apphub.service.community.friendship.dao.request;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class FriendRequestDao extends AbstractDao<FriendRequestEntity, FriendRequest, String, FriendRequestRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    public FriendRequestDao(FriendRequestConverter converter, FriendRequestRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteBySenderIdAndReceiverId(UUID senderId, UUID receiverId) {
        repository.deleteBySenderIdAndReceiverId(uuidConverter.convertDomain(senderId), uuidConverter.convertDomain(receiverId));
    }

    public List<FriendRequest> getBySenderIdOrReceiverId(UUID userId) {
        return converter.convertEntity(repository.getBySenderIdOrReceiverId(uuidConverter.convertDomain(userId)));
    }

    public List<FriendRequest> getBySenderId(UUID userId) {
        return converter.convertEntity(repository.getBySenderId(uuidConverter.convertDomain(userId)));
    }

    public List<FriendRequest> getByReceiverId(UUID userId) {
        return converter.convertEntity(repository.getByReceiverId(uuidConverter.convertDomain(userId)));
    }

    public Optional<FriendRequest> findBySenderIdAndReceiverId(UUID senderId, UUID receiverId) {
        return converter.convertEntity(repository.findBySenderIdAndReceiverId(uuidConverter.convertDomain(senderId), uuidConverter.convertDomain(receiverId)));
    }

    public Optional<FriendRequest> findById(UUID friendRequestId) {
        return findById(uuidConverter.convertDomain(friendRequestId));
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }
}

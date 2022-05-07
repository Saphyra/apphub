package com.github.saphyra.apphub.service.community.friendship.dao.friend;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class FriendshipDao extends AbstractDao<FriendshipEntity, Friendship, String, FriendshipRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    public FriendshipDao(FriendshipConverter converter, FriendshipRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteByUserIdAndFriendId(UUID userId, UUID friendId) {
        repository.deleteByUserIdAndFriendId(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(friendId));
    }

    public List<Friendship> getByUserIdOrFriendId(UUID userId) {
        return converter.convertEntity(repository.getByUserIdOrFriendId(uuidConverter.convertDomain(userId)));
    }

    public Optional<Friendship> findByUserIdAndFriendId(UUID userId, UUID friendId) {
        return converter.convertEntity(repository.findByUserIdAndFriendId(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(friendId)));
    }

    public Optional<Friendship> findById(UUID friendshipId) {
        return findById(uuidConverter.convertDomain(friendshipId));
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }
}

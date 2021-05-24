package com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao;

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

    public List<Friendship> getByFriendId(UUID userId) {
        return converter.convertEntity(repository.getByFriendId(uuidConverter.convertDomain(userId)));
    }

    public Optional<Friendship> findById(UUID friendshipId) {
        return converter.convertEntity(repository.findById(uuidConverter.convertDomain(friendshipId)));
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByFriendId(uuidConverter.convertDomain(userId));
    }

    public Optional<Friendship> findByFriendIds(UUID friend1, UUID friend2) {
        return converter.convertEntity(repository.findByFriendIds(
            uuidConverter.convertDomain(friend1),
            uuidConverter.convertDomain(friend2)
        ));
    }
}

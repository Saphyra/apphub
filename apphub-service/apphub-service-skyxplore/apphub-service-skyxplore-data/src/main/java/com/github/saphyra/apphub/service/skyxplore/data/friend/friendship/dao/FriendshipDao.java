package com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.Converter;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
//TODO unit test
public class FriendshipDao extends AbstractDao<FriendshipEntity, Friendship, String, FriendshipRepository> {
    private final UuidConverter uuidConverter;

    public FriendshipDao(Converter<FriendshipEntity, Friendship> converter, FriendshipRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public List<Friendship> getByFriendId(UUID userId) {
        return converter.convertEntity(repository.getByFriendId(uuidConverter.convertDomain(userId)));
    }

    public Optional<Friendship> findById(UUID userId) {
        return converter.convertEntity(repository.findById(uuidConverter.convertDomain(userId)));
    }
}

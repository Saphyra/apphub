package com.github.saphyra.apphub.service.community.friendship.dao.friend;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import org.springframework.stereotype.Component;

@Component
public class FriendshipDao extends AbstractDao<FriendshipEntity, Friendship, String, FriendshipRepository> {
    public FriendshipDao(FriendshipConverter converter, FriendshipRepository repository) {
        super(converter, repository);
    }
}

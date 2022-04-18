package com.github.saphyra.apphub.service.community.friendship.dao.request;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FriendRequestDao extends AbstractDao<FriendRequestEntity, FriendRequest, String, FriendRequestRepository> {
    public FriendRequestDao(FriendRequestConverter converter, FriendRequestRepository repository) {
        super(converter, repository);
    }
}

package com.github.saphyra.apphub.service.community.blacklist.service;

import com.github.saphyra.apphub.service.community.blacklist.dao.BlacklistDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class BlockedUsersQueryService {
    private final BlacklistDao blacklistDao;

    public List<UUID> getUserIdsCannotContactWith(UUID userId) {
        return blacklistDao.getByUserIdOrBlockedUserId(userId)
            .stream()
            .map(blacklist -> blacklist.getOtherUserId(userId))
            .collect(Collectors.toList());
    }
}

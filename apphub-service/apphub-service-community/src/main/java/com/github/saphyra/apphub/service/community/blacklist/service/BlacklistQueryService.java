package com.github.saphyra.apphub.service.community.blacklist.service;

import com.github.saphyra.apphub.api.community.model.response.blacklist.BlacklistResponse;
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
public class BlacklistQueryService {
    private final BlacklistDao blacklistDao;
    private final BlacklistToResponseConverter blacklistToResponseConverter;

    public List<BlacklistResponse> getBlacklist(UUID userId) {
        return blacklistDao.getByUserId(userId)
            .stream()
            .map(blacklistToResponseConverter::convert)
            .collect(Collectors.toList());
    }
}

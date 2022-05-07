package com.github.saphyra.apphub.service.community.blacklist;

import com.github.saphyra.apphub.api.community.model.response.blacklist.BlacklistResponse;
import com.github.saphyra.apphub.api.community.model.response.SearchResultItem;
import com.github.saphyra.apphub.api.community.server.BlacklistController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.community.blacklist.service.BlacklistCreationService;
import com.github.saphyra.apphub.service.community.blacklist.service.BlacklistDeletionService;
import com.github.saphyra.apphub.service.community.blacklist.service.BlacklistQueryService;
import com.github.saphyra.apphub.service.community.blacklist.service.BlacklistSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BlacklistControllerImpl implements BlacklistController {
    private final BlacklistSearchService blacklistSearchService;
    private final BlacklistQueryService blacklistQueryService;
    private final BlacklistCreationService blacklistCreationService;
    private final BlacklistDeletionService blacklistDeletionService;

    @Override
    public List<SearchResultItem> search(OneParamRequest<String> queryString, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to query users to blacklist based on text {}", accessTokenHeader.getUserId(), queryString.getValue());
        return blacklistSearchService.search(accessTokenHeader.getUserId(), queryString.getValue());
    }

    @Override
    public List<BlacklistResponse> getBlacklist(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know his blacklist", accessTokenHeader.getUserId());
        return blacklistQueryService.getBlacklist(accessTokenHeader.getUserId());
    }

    @Override
    public BlacklistResponse create(OneParamRequest<UUID> blockedUserId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to add {} to his blacklist", accessTokenHeader.getUserId(), blockedUserId.getValue());
        return blacklistCreationService.create(accessTokenHeader.getUserId(), blockedUserId.getValue());
    }

    @Override
    public void delete(UUID blacklistId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete blacklist {}", accessTokenHeader.getUserId(), blacklistId);
        blacklistDeletionService.delete(accessTokenHeader.getUserId(), blacklistId);
    }
}

package com.github.saphyra.apphub.service.user.ban;

import com.github.saphyra.apphub.api.etc.user.model.ban.BanRequest;
import com.github.saphyra.apphub.api.etc.user.model.ban.BannedDetailsRequest;
import com.github.saphyra.apphub.api.etc.user.model.ban.BannedDetailsResponse;
import com.github.saphyra.apphub.api.etc.user.model.ban.MarkUserForDeletionRequest;
import com.github.saphyra.apphub.api.etc.user.model.ban.BanResponse;
import com.github.saphyra.apphub.api.etc.user.model.ban.BanSearchResponse;
import com.github.saphyra.apphub.api.etc.user.server.BanController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.user.ban.service.BanResponseQueryService;
import com.github.saphyra.apphub.service.user.ban.service.BanSearchService;
import com.github.saphyra.apphub.service.user.ban.service.BanService;
import com.github.saphyra.apphub.service.user.ban.service.MarkUserForDeletionService;
import com.github.saphyra.apphub.service.user.ban.service.BannedDetailsQueryService;
import com.github.saphyra.apphub.service.user.ban.service.RevokeBanService;
import com.github.saphyra.apphub.service.user.ban.service.UnmarkUserForDeletionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BanControllerImpl implements BanController {
    private final BanService banService;
    private final RevokeBanService revokeBanService;
    private final BanResponseQueryService banResponseQueryService;
    private final MarkUserForDeletionService markUserForDeletionService;
    private final UnmarkUserForDeletionService unmarkUserForDeletionService;
    private final BanSearchService banSearchService;
    private final BannedDetailsQueryService bannedDetailsQueryService;

    @Override
    public BanResponse banUser(BanRequest request, AccessToken accessToken) {
        log.info("{} sent banRequest: {}", accessToken.getUserId(), request);
        return banService.ban(accessToken.getUserId(), request);
    }

    @Override
    public BanResponse revokeBan(OneParamRequest<String> password, UUID banId, AccessToken accessToken) {
        log.info("{} wants to revoke ban {}", accessToken.getUserId(), banId);
        return revokeBanService.revokeBan(accessToken.getUserId(), password.getValue(), banId);
    }

    @Override
    public BanResponse getBans(UUID bannedUserId, AccessToken accessToken) {
        log.info("{} wants to know the bans of user {}", accessToken.getUserId(), bannedUserId);
        return banResponseQueryService.getBans(bannedUserId);
    }

    @Override
    public BanResponse markUserForDeletion(MarkUserForDeletionRequest request, UUID deletedUserId, AccessToken accessToken) {
        log.info("{} wants to mark {} for deletion at {}", accessToken.getUserId(), deletedUserId, request.getMarkForDeletionAt());
        return markUserForDeletionService.markUserForDeletion(deletedUserId, request, accessToken.getUserId());
    }

    @Override
    public BanResponse unmarkUserForDeletion(UUID deletedUserId, AccessToken accessToken) {
        log.info("{} wants to unmark {} for deletion", accessToken.getUserId(), deletedUserId);
        return unmarkUserForDeletionService.unmarkUserForDeletion(deletedUserId);
    }

    @Override
    public List<BanSearchResponse> search(OneParamRequest<String> query, AccessToken accessToken) {
        log.info("{} wants to search for user {}", accessToken.getUserId(), query.getValue());
        return banSearchService.search(query.getValue());
    }

    @Override
    public BannedDetailsResponse getBannedDetails(BannedDetailsRequest request) {
        log.info("Querying bannedDetails for {}", request);

        return bannedDetailsQueryService.getBannedDetails(request.getUserId(), request.getRequiredRoles());
    }
}

package com.github.saphyra.apphub.service.user.ban;

import com.github.saphyra.apphub.api.user.model.request.BanRequest;
import com.github.saphyra.apphub.api.user.model.request.MarkUserForDeletionRequest;
import com.github.saphyra.apphub.api.user.model.response.BanResponse;
import com.github.saphyra.apphub.api.user.server.BanController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.user.ban.service.BanResponseQueryService;
import com.github.saphyra.apphub.service.user.ban.service.BanService;
import com.github.saphyra.apphub.service.user.ban.service.MarkUserForDeletionService;
import com.github.saphyra.apphub.service.user.ban.service.RevokeBanService;
import com.github.saphyra.apphub.service.user.ban.service.UnmarkUserForDeletionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

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

    @Override
    public void banUser(BanRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} sent banRequest: {}", accessTokenHeader.getUserId(), request);
        banService.ban(accessTokenHeader.getUserId(), request);
    }

    @Override
    public void revokeBan(OneParamRequest<String> password, UUID banId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to revoke ban {}", accessTokenHeader.getUserId(), banId);
        revokeBanService.revokeBan(accessTokenHeader.getUserId(), password.getValue(), banId);
    }

    @Override
    public BanResponse getBans(UUID bannedUserId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the bans of user {}", accessTokenHeader.getUserId(), bannedUserId);
        return banResponseQueryService.getBans(bannedUserId);
    }

    @Override
    public BanResponse markUserForDeletion(MarkUserForDeletionRequest request, UUID deletedUserId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to mark {} for deletion at {}", accessTokenHeader.getUserId(), deletedUserId, request);
        return markUserForDeletionService.markUserForDeletion(deletedUserId, request, accessTokenHeader.getUserId());
    }

    @Override
    public BanResponse unmarkUserForDeletion(UUID deletedUserId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to unmark {} for deletion", accessTokenHeader.getUserId(), deletedUserId);
        return unmarkUserForDeletionService.unmarkUserForDeletion(deletedUserId);
    }
}

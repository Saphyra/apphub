package com.github.saphyra.apphub.service.skyxplore.lobby.controller;

import com.github.saphyra.apphub.api.skyxplore.lobby.server.SkyXploreLobbyController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.ExitFromLobbyService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.invite.InvitationService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.creation.LobbyCreationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SkyXploreLobbyControllerImpl implements SkyXploreLobbyController {
    private final ExitFromLobbyService exitFromLobbyService;
    private final LobbyCreationService lobbyCreationService;
    private final InvitationService invitationService;

    @Override
    //TODO unit test
    //TODO int test
    //TODO API test
    public void createLobbyIfNotExists(AccessTokenHeader accessTokenHeader) {
        log.info("Creating lobby for user {} if not exists", accessTokenHeader.getUserId());
        lobbyCreationService.createIfNotExists(accessTokenHeader.getUserId());
    }

    @Override
    public void exitFromLobby(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to exit from lobby", accessTokenHeader.getUserId());
        exitFromLobbyService.exit(accessTokenHeader.getUserId());
    }

    @Override
    public void inviteToLobby(UUID friendId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to invite {} to lobby.", accessTokenHeader.getUserId(), friendId);
        invitationService.invite(accessTokenHeader.getUserId(), friendId);
    }
}

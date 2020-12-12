package com.github.saphyra.apphub.service.skyxplore.lobby.controller;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.skyxplore.lobby.server.SkyXploreLobbyController;
import com.github.saphyra.apphub.api.skyxplore.response.LobbyMembersResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.ExitFromLobbyService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.JoinToLobbyService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.member.LobbyMemberQueryService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.creation.LobbyCreationService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.event.WebSocketEventHandlerService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.invite.InvitationService;
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
    private final JoinToLobbyService joinToLobbyService;
    private final WebSocketEventHandlerService webSocketEventHandlerService;
    private final LobbyMemberQueryService lobbyMemberQueryService;

    @Override
    //TODO unit test
    //TODO int test
    public void createLobbyIfNotExists(AccessTokenHeader accessTokenHeader) {//TODO always enter to a new lobby, and remove from the existing one
        log.info("Creating lobby for user {} if not exists", accessTokenHeader.getUserId());
        lobbyCreationService.createIfNotExists(accessTokenHeader.getUserId());
    }

    @Override
    //TODO unit test
    //TODO int test
    //TODO API test
    public void exitFromLobby(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to exit from lobby", accessTokenHeader.getUserId());
        exitFromLobbyService.exit(accessTokenHeader.getUserId());
    }

    @Override
    //TODO unit test
    //TODO int test
    //TODO API test
    public void inviteToLobby(UUID friendId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to invite {} to lobby.", accessTokenHeader.getUserId(), friendId);
        invitationService.invite(accessTokenHeader.getUserId(), friendId);
    }

    @Override
    //TODO unit test
    //TODO int test
    //TODO API test
    public void joinLobby(UUID invitorId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to join to lobby of {}", accessTokenHeader.getUserId(), invitorId);
        joinToLobbyService.join(accessTokenHeader.getUserId(), invitorId);
    }

    @Override
    //TODO unit test
    //TODO int test
    public void processWebSocketEvent(UUID from, WebSocketEvent event) {
        log.info("Handling event {} from {}", event.getEventName(), from);
        webSocketEventHandlerService.handle(from, event);
    }

    @Override
    //TODO unit test
    //TODO int test
    public void userJoinedToLobby(UUID userId) {
        log.info("User {} is joined to lobby.", userId);
        joinToLobbyService.sendJoinedNotification(userId);
    }

    @Override
    //TODO unit test
    //TODO int test
    //TODO API test
    public LobbyMembersResponse getMembersOfLobby(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the members of his lobby.", accessTokenHeader.getUserId());
        return lobbyMemberQueryService.getMembers(accessTokenHeader.getUserId());
    }
}

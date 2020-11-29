package com.github.saphyra.apphub.api.skyxplore.lobby.server;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

public interface SkyXploreLobbyController {
    @PutMapping(Endpoints.SKYXPLORE_CREATE_LOBBY)
    void createLobbyIfNotExists(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(Endpoints.SKYXPLORE_EXIT_FROM_LOBBY)
    void exitFromLobby(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.SKYXPLORE_INVITE_TO_LOBBY)
    void inviteToLobby(@PathVariable("friendId") UUID friendId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}

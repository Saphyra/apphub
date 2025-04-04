package com.github.saphyra.apphub.api.skyxplore.game.server.platform;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.api.skyxplore.request.CreateChatRoomRequest;
import com.github.saphyra.apphub.api.skyxplore.response.game.ChatRoomResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.endpoints.skyxplore.SkyXploreGameEndpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface SkyXploreGameChatController {
    /**
     * Listing all the players connected to the game
     */
    @GetMapping(SkyXploreGameEndpoints.SKYXPLORE_GAME_GET_PLAYERS)
    List<SkyXploreCharacterModel> getPlayers(@RequestParam(value = "excludeSelf", required = false, defaultValue = "false") Boolean excludeSelf, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PutMapping(SkyXploreGameEndpoints.SKYXPLORE_GAME_CREATE_CHAT_ROOM)
    void createChatRoom(@RequestBody CreateChatRoomRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(SkyXploreGameEndpoints.SKYXPLORE_GAME_LEAVE_CHAT_ROOM)
    void leaveChatRoom(@PathVariable("roomId") String roomId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(SkyXploreGameEndpoints.SKYXPLORE_GAME_GET_CHAT_ROOMS)
    List<ChatRoomResponse> getChatRooms(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}

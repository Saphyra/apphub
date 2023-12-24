package com.github.saphyra.apphub.service.skyxplore.game.service.chat;

import com.github.saphyra.apphub.api.skyxplore.game.server.SkyXploreGameChatController;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.api.skyxplore.request.CreateChatRoomRequest;
import com.github.saphyra.apphub.api.skyxplore.response.game.ChatRoomResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.player.Player;
import com.github.saphyra.apphub.service.skyxplore.game.service.chat.create.CreateChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SkyXploreGameChatControllerImpl implements SkyXploreGameChatController {
    private final GameDao gameDao;
    private final CreateChatRoomService createChatRoomService;
    private final LeaveChatRoomService leaveChatRoomService;

    @Override
    public List<SkyXploreCharacterModel> getPlayers(Boolean excludeSelf, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the players of game.", accessTokenHeader.getUserId());
        return gameDao.findByUserIdValidated(accessTokenHeader.getUserId())
            .getPlayers()
            .values()
            .stream()
            .filter(player -> !player.isAi())
            .filter(Player::isConnected)
            .filter(player -> !excludeSelf || !player.getUserId().equals(accessTokenHeader.getUserId()))
            .map(player -> SkyXploreCharacterModel.builder()
                .id(player.getUserId())
                .name(player.getPlayerName())
                .build()
            )
            .collect(Collectors.toList());
    }

    @Override
    public void createChatRoom(CreateChatRoomRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create a chat room for members {}", accessTokenHeader.getUserId(), request.getMembers());
        createChatRoomService.createChatRoom(accessTokenHeader.getUserId(), request);
    }

    @Override
    public void leaveChatRoom(String roomId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to leave room {}", accessTokenHeader.getUserId(), roomId);
        leaveChatRoomService.leave(accessTokenHeader.getUserId(), roomId);
    }

    @Override
    //TODO unit test
    public List<ChatRoomResponse> getChatRooms(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know his chat rooms.", accessTokenHeader.getUserId());

        return gameDao.findByUserIdValidated(accessTokenHeader.getUserId())
            .getChat()
            .getRooms()
            .stream()
            .filter(chatRoom -> chatRoom.getMembers().contains(accessTokenHeader.getUserId()))
            .map(chatRoom -> ChatRoomResponse.builder()
                .roomId(chatRoom.getId())
                .roomTitle(chatRoom.getRoomTitle())
                .build())
            .collect(Collectors.toList());
    }
}

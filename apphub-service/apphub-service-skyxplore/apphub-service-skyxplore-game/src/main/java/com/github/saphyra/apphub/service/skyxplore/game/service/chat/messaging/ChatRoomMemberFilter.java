package com.github.saphyra.apphub.service.skyxplore.game.service.chat.messaging;

import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.ChatRoom;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Player;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class ChatRoomMemberFilter {
    List<UUID> getMembers(UUID from, String room, List<ChatRoom> chatRooms, Map<UUID, Player> players) {
        if (GameConstants.CHAT_ROOM_GENERAL.equals(room)) {
            return filterConnectedPlayers(players.values());
        }

        if (GameConstants.CHAT_ROOM_ALLIANCE.equals(room)) {
            Optional<ChatRoom> chatRoom = findAllianceRoomOf(from, chatRooms);

            return chatRoom.map(ChatRoom::getMembers)
                .map(userIds -> filterConnectedPlayers(userIds, players))
                .orElse(Arrays.asList(from));
        }

        return findRoom(room, chatRooms)
            .map(chatRoom -> filterConnectedPlayers(chatRoom.getMembers(), players))
            .orElse(Arrays.asList(from));

    }

    private Optional<ChatRoom> findAllianceRoomOf(UUID from, List<ChatRoom> chatRooms) {
        return chatRooms.stream()
            .filter(chatRoom -> GameConstants.CHAT_ROOM_ALLIANCE.equals(chatRoom.getId()))
            .filter(chatRoom -> chatRoom.getMembers().contains(from))
            .findFirst();
    }

    private List<UUID> filterConnectedPlayers(Collection<Player> players) {
        return players.stream()
            .filter(Player::isConnected)
            .map(Player::getUserId)
            .collect(Collectors.toList());
    }

    private List<UUID> filterConnectedPlayers(List<UUID> userIds, Map<UUID, Player> players) {
        return userIds.stream()
            .map(players::get)
            .filter(Player::isConnected)
            .map(Player::getUserId)
            .collect(Collectors.toList());
    }

    private Optional<ChatRoom> findRoom(String room, List<ChatRoom> chatRooms) {
        return chatRooms.stream()
            .filter(chatRoom -> chatRoom.getId().equals(room))
            .findFirst();
    }
}

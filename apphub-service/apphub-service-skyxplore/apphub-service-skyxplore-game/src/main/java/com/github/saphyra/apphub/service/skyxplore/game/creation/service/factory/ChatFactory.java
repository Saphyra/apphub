package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory;

import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.Chat;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.ChatRoom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ChatFactory {
    public Chat create(Map<UUID, UUID> players) {

        List<ChatRoom> chatRooms = new ArrayList<>();

        chatRooms.add(createGeneral(players.keySet()));
        chatRooms.addAll(createAlliances(players));

        return Chat.builder()
            .players(new ArrayList<>(players.keySet()))
            .rooms(chatRooms)
            .build();
    }

    private List<ChatRoom> createAlliances(Map<UUID, UUID> players) {
        Map<UUID, List<UUID>> alliances = new HashMap<>();
        for (Map.Entry<UUID, UUID> entry : players.entrySet()) {
            if (isNull(entry.getValue())) {
                alliances.put(UUID.randomUUID(), Arrays.asList(entry.getKey()));
            } else {
                if (!alliances.containsKey(entry.getValue())) {
                    alliances.put(entry.getKey(), new ArrayList<>());
                }
                alliances.get(entry.getValue()).add(entry.getKey());
            }
        }

        return alliances.values()
            .stream()
            .map(members -> createRoom(GameConstants.CHAT_ROOM_ALLIANCE, members))
            .collect(Collectors.toList());
    }

    private ChatRoom createGeneral(Set<UUID> members) {
        return createRoom(GameConstants.CHAT_ROOM_GENERAL, members);
    }

    private ChatRoom createRoom(String id, Collection<UUID> members) {
        return ChatRoom.builder()
            .id(id)
            .members(new ArrayList<>(members))
            .build();
    }
}

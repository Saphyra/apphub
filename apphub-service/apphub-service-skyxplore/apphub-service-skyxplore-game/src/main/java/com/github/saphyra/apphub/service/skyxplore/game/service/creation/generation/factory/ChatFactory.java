package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.Chat;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.ChatRoom;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Player;
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
public class ChatFactory {
    private final IdGenerator idGenerator;

    public Chat create(Collection<Player> values) {
        List<Player> players = values.stream()
            .filter(player -> !player.isAi())
            .collect(Collectors.toList());
        return create(CollectionUtils.toMap(Player::getUserId, Player::getAllianceId, players));
    }

    /**
     * @param players <UserId, AllianceId>
     */
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
        Map<UUID, List<UUID>> alliances = new HashMap<>(); //<AllianceId, List<PlayerId>>
        for (Map.Entry<UUID, UUID> entry : players.entrySet()) {
            if (isNull(entry.getValue())) {
                alliances.put(idGenerator.randomUuid(), Arrays.asList(entry.getKey()));
            } else {
                if (!alliances.containsKey(entry.getValue())) {
                    alliances.put(entry.getValue(), new ArrayList<>());
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

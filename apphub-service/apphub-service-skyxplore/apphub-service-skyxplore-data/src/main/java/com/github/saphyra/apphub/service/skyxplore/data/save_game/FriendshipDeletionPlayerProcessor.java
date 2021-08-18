package com.github.saphyra.apphub.service.skyxplore.data.save_game;

import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.game.GameDao;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.player.PlayerDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class FriendshipDeletionPlayerProcessor {
    private final GameDao gameDao;
    private final PlayerDao playerDao;

    public void processDeletedFriendship(UUID userId1, UUID userId2) {
        Stream.of(userId1, userId2)
            .forEach(userId -> process(userId, otherUserId(userId, userId1, userId2)));
    }

    private void process(UUID userId, UUID otherUserId) {
        gameDao.getByHost(userId)
            .stream()
            .flatMap(gameModel -> playerDao.getByGameId(gameModel.getId()).stream())
            .filter(playerModel -> otherUserId.equals(playerModel.getUserId()))
            .peek(playerModel -> playerModel.setAi(true))
            .forEach(playerDao::save);
    }

    private UUID otherUserId(UUID userId, UUID userId1, UUID userId2) {
        return userId.equals(userId1) ? userId2 : userId1;
    }
}

package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.game;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@Slf4j
public class GameRepositoryTest {
    private static final String GAME_ID_1 = "game-id-1";
    private static final String GAME_ID_2 = "game-id-2";
    private static final String HOST_1 = "host-1";
    private static final String HOST_2 = "host-2";

    @Autowired
    private GameRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    public void getByHost() {
        GameEntity entity1 = GameEntity.builder()
            .gameId(GAME_ID_1)
            .host(HOST_1)
            .build();
        GameEntity entity2 = GameEntity.builder()
            .gameId(GAME_ID_2)
            .host(HOST_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        List<GameEntity> result = underTest.getByHost(HOST_1);

        assertThat(result).containsExactly(entity1);
    }

    @Test
    public void getGamesMarkedForDeletion() {
        GameEntity entity1 = GameEntity.builder()
            .gameId(GAME_ID_1)
            .markedForDeletion(true)
            .build();
        GameEntity entity2 = GameEntity.builder()
            .gameId(GAME_ID_2)
            .markedForDeletion(false)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        List<GameEntity> result = underTest.getGamesMarkedForDeletion();

        assertThat(result).containsExactly(entity1);
    }
}
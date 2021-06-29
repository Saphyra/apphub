package com.github.saphyra.apphub.service.skyxplore.data.save_game.player;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@Slf4j
public class PlayerRepositoryTest {
    private static final String GAME_ID_1 = "game-id-1";
    private static final String GAME_ID_2 = "game-id-2";
    private static final String PLAYER_ID_1 = "player-id-1";
    private static final String PLAYER_ID_2 = "player-id-2";
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";

    @Autowired
    private PlayerRepository underTest;

    @After
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    public void deleteByGameId() {
        PlayerEntity entity1 = PlayerEntity.builder()
            .playerId(PLAYER_ID_1)
            .gameId(GAME_ID_1)
            .build();
        PlayerEntity entity2 = PlayerEntity.builder()
            .playerId(PLAYER_ID_2)
            .gameId(GAME_ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        underTest.deleteByGameId(GAME_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    public void getByUserId() {
        PlayerEntity entity1 = PlayerEntity.builder()
            .playerId(PLAYER_ID_1)
            .userId(USER_ID_1)
            .build();
        PlayerEntity entity2 = PlayerEntity.builder()
            .playerId(PLAYER_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        List<PlayerEntity> result = underTest.getByUserId(USER_ID_1);

        assertThat(result).containsExactly(entity1);
    }

    @Test
    public void getByGameId() {
        PlayerEntity entity1 = PlayerEntity.builder()
            .playerId(PLAYER_ID_1)
            .userId(USER_ID_1)
            .gameId(GAME_ID_1)
            .build();
        PlayerEntity entity2 = PlayerEntity.builder()
            .playerId(PLAYER_ID_2)
            .userId(USER_ID_2)
            .gameId(GAME_ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        List<PlayerEntity> result = underTest.getByGameId(GAME_ID_1);

        assertThat(result).containsExactly(entity1);
    }
}
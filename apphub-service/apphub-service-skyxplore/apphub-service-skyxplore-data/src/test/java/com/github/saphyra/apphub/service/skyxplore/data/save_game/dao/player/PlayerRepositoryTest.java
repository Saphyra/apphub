package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.player;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@Slf4j
public class PlayerRepositoryTest {
    private static final String GAME_ID_1 = "game-id-1";
    private static final String GAME_ID_2 = "game-id-2";
    private static final String PLAYER_ID_1 = "player-id-1";
    private static final String PLAYER_ID_2 = "player-id-2";
    private static final String PLAYER_ID_3 = "player-id-3";
    private static final String PLAYER_ID_4 = "player-id-4";
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";

    @Autowired
    private PlayerRepository underTest;

    @AfterEach
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

    @Test
    public void getByGameId_paged() {
        PlayerEntity entity1 = PlayerEntity.builder()
            .playerId(PLAYER_ID_1)
            .gameId(GAME_ID_1)
            .build();
        PlayerEntity entity2 = PlayerEntity.builder()
            .playerId(PLAYER_ID_2)
            .gameId(GAME_ID_1)
            .build();
        PlayerEntity entity3 = PlayerEntity.builder()
            .playerId(PLAYER_ID_3)
            .gameId(GAME_ID_1)
            .build();
        PlayerEntity entity4 = PlayerEntity.builder()
            .playerId(PLAYER_ID_4)
            .gameId(GAME_ID_2)
            .build();

        underTest.saveAll(List.of(entity1, entity2, entity3, entity4));

        assertThat(underTest.getByGameId(GAME_ID_1, PageRequest.of(0, 2))).containsExactly(entity1, entity2);
        assertThat(underTest.getByGameId(GAME_ID_1, PageRequest.of(1, 2))).containsExactly(entity3);
        assertThat(underTest.getByGameId(GAME_ID_1, PageRequest.of(2, 2))).isEmpty();
    }
}
package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.alliance;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
public class AllianceRepositoryTest {
    private static final String ALLIANCE_ID_1 = "alliance-id-1";
    private static final String ALLIANCE_ID_2 = "alliance-id-2";
    private static final String GAME_ID_1 = "game-id-1";
    private static final String GAME_ID_2 = "game-id-2";

    @Autowired
    private AllianceRepository underTest;

    @After
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    public void deleteByGameId() {
        AllianceEntity entity1 = AllianceEntity.builder()
            .allianceId(ALLIANCE_ID_1)
            .gameId(GAME_ID_1)
            .build();
        AllianceEntity entity2 = AllianceEntity.builder()
            .allianceId(ALLIANCE_ID_2)
            .gameId(GAME_ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        underTest.deleteByGameId(GAME_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    public void getByGameId() {
        AllianceEntity entity1 = AllianceEntity.builder()
            .allianceId(ALLIANCE_ID_1)
            .gameId(GAME_ID_1)
            .build();
        AllianceEntity entity2 = AllianceEntity.builder()
            .allianceId(ALLIANCE_ID_2)
            .gameId(GAME_ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        List<AllianceEntity> result = underTest.getByGameId(GAME_ID_1);

        assertThat(result).containsExactly(entity1);
    }
}
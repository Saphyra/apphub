package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.citizen;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import jakarta.transaction.Transactional;
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
public class CitizenRepositoryTest {
    private static final String GAME_ID_1 = "game-id-1";
    private static final String GAME_ID_2 = "game-id-2";
    private static final String CITIZEN_ID_1 = "citizen-id-1";
    private static final String CITIZEN_ID_2 = "citizen-id-2";
    private static final String LOCATION_1 = "location-1";
    private static final String LOCATION_2 = "location-2";

    @Autowired
    private CitizenRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    public void deleteByGameId() {
        CitizenEntity entity1 = CitizenEntity.builder()
            .citizenId(CITIZEN_ID_1)
            .gameId(GAME_ID_1)
            .build();
        CitizenEntity entity2 = CitizenEntity.builder()
            .citizenId(CITIZEN_ID_2)
            .gameId(GAME_ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        underTest.deleteByGameId(GAME_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    public void getByLocation() {
        CitizenEntity entity1 = CitizenEntity.builder()
            .citizenId(CITIZEN_ID_1)
            .gameId(GAME_ID_1)
            .location(LOCATION_1)
            .build();
        CitizenEntity entity2 = CitizenEntity.builder()
            .citizenId(CITIZEN_ID_2)
            .gameId(GAME_ID_2)
            .location(LOCATION_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        List<CitizenEntity> result = underTest.getByLocation(LOCATION_1);

        assertThat(result).containsExactly(entity1);
    }
}
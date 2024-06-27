package com.github.saphyra.apphub.service.skyxplore.data.setting.dao;

import com.github.saphyra.apphub.api.skyxplore.model.data.setting.SettingType;
import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@Slf4j
class SettingRepositoryTest {
    private static final String SETTING_ID_1 = "setting-id-1";
    private static final String SETTING_ID_2 = "setting-id-2";
    private static final String GAME_ID_1 = "game-id-1";
    private static final String GAME_ID_2 = "game-id-2";
    private static final String LOCATION_1 = "location-1";
    private static final String LOCATION_2 = "location-2";
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";

    @Autowired
    private SettingRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    void findByGameIdAndTypeAndLocation_found() {
        SettingEntity entity = SettingEntity.builder()
            .settingId(SETTING_ID_1)
            .userId(USER_ID_1)
            .gameId(GAME_ID_1)
            .type(SettingType.POPULATION_HIDE)
            .location(LOCATION_1)
            .build();
        underTest.save(entity);

        assertThat(underTest.findByUserIdAndGameIdAndTypeAndLocation(USER_ID_1, GAME_ID_1, SettingType.POPULATION_HIDE, LOCATION_1)).contains(entity);
    }

    @Test
    void findByGameIdAndTypeAndLocation_differentUserId() {
        SettingEntity entity = SettingEntity.builder()
            .settingId(SETTING_ID_1)
            .userId(USER_ID_2)
            .gameId(GAME_ID_1)
            .type(SettingType.POPULATION_HIDE)
            .location(LOCATION_1)
            .build();
        underTest.save(entity);

        assertThat(underTest.findByUserIdAndGameIdAndTypeAndLocation(USER_ID_1, GAME_ID_1, SettingType.POPULATION_HIDE, LOCATION_1)).isEmpty();
    }

    @Test
    void findByGameIdAndTypeAndLocation_differentGameId() {
        SettingEntity entity = SettingEntity.builder()
            .settingId(SETTING_ID_1)
            .userId(USER_ID_1)
            .gameId(GAME_ID_2)
            .type(SettingType.POPULATION_HIDE)
            .location(LOCATION_1)
            .build();
        underTest.save(entity);

        assertThat(underTest.findByUserIdAndGameIdAndTypeAndLocation(USER_ID_1, GAME_ID_1, SettingType.POPULATION_HIDE, LOCATION_1)).isEmpty();
    }

    @Test
    void findByGameIdAndTypeAndLocation_differentType() {
        SettingEntity entity = SettingEntity.builder()
            .settingId(SETTING_ID_1)
            .userId(USER_ID_1)
            .gameId(GAME_ID_1)
            .type(SettingType.POPULATION_ORDER)
            .location(LOCATION_1)
            .build();
        underTest.save(entity);

        assertThat(underTest.findByUserIdAndGameIdAndTypeAndLocation(USER_ID_1, GAME_ID_1, SettingType.POPULATION_HIDE, LOCATION_1)).isEmpty();
    }

    @Test
    void findByGameIdAndTypeAndLocation_differentLocation() {
        SettingEntity entity = SettingEntity.builder()
            .settingId(SETTING_ID_1)
            .userId(USER_ID_1)
            .gameId(GAME_ID_1)
            .type(SettingType.POPULATION_HIDE)
            .location(LOCATION_2)
            .build();
        underTest.save(entity);

        assertThat(underTest.findByUserIdAndGameIdAndTypeAndLocation(USER_ID_1, GAME_ID_1, SettingType.POPULATION_HIDE, LOCATION_1)).isEmpty();
    }

    @Test
    @Transactional
    void deleteByUserId() {
        SettingEntity entity1 = SettingEntity.builder()
            .settingId(SETTING_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);

        SettingEntity entity2 = SettingEntity.builder()
            .settingId(SETTING_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByUserId(USER_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    @Transactional
    void deleteByGameId() {
        SettingEntity entity1 = SettingEntity.builder()
            .settingId(SETTING_ID_1)
            .gameId(GAME_ID_1)
            .build();
        underTest.save(entity1);

        SettingEntity entity2 = SettingEntity.builder()
            .settingId(SETTING_ID_2)
            .gameId(GAME_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByGameId(GAME_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }
}
package com.github.saphyra.apphub.service.skyxplore.data.save_game.storage_settings;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.StorageSettingModel;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class StorageSettingConverterTest {
    private static final UUID STORAGE_SETTING_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String LOCATION_TYPE = "location-type";
    private static final String DATA_ID = "data-id";
    private static final Integer TARGET_AMOUNT = 345;
    private static final Integer PRIORITY = 254;
    private static final Integer BATCH_SIZE = 65163;
    private static final String STORAGE_SETTING_ID_STRING = "storage-setting-id";
    private static final String GAME_ID_STRING = "game-id";
    private static final String LOCATION_STRING = "location";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private StorageSettingConverter underTest;

    @Test
    public void convertDomain() {
        StorageSettingModel model = new StorageSettingModel();
        model.setId(STORAGE_SETTING_ID);
        model.setGameId(GAME_ID);
        model.setLocation(LOCATION);
        model.setLocationType(LOCATION_TYPE);
        model.setDataId(DATA_ID);
        model.setTargetAmount(TARGET_AMOUNT);
        model.setPriority(PRIORITY);
        model.setBatchSize(BATCH_SIZE);

        given(uuidConverter.convertDomain(STORAGE_SETTING_ID)).willReturn(STORAGE_SETTING_ID_STRING);
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(uuidConverter.convertDomain(LOCATION)).willReturn(LOCATION_STRING);

        StorageSettingEntity result = underTest.convertDomain(model);

        assertThat(result.getStorageSettingId()).isEqualTo(STORAGE_SETTING_ID_STRING);
        assertThat(result.getGameId()).isEqualTo(GAME_ID_STRING);
        assertThat(result.getLocation()).isEqualTo(LOCATION_STRING);
        assertThat(result.getLocationType()).isEqualTo(LOCATION_TYPE);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getTargetAmount()).isEqualTo(TARGET_AMOUNT);
        assertThat(result.getPriority()).isEqualTo(PRIORITY);
        assertThat(result.getBatchSize()).isEqualTo(BATCH_SIZE);
    }

    @Test
    public void convertEntity() {
        StorageSettingEntity entity = StorageSettingEntity.builder()
            .storageSettingId(STORAGE_SETTING_ID_STRING)
            .gameId(GAME_ID_STRING)
            .location(LOCATION_STRING)
            .locationType(LOCATION_TYPE)
            .dataId(DATA_ID)
            .targetAmount(TARGET_AMOUNT)
            .priority(PRIORITY)
            .batchSize(BATCH_SIZE)
            .build();

        given(uuidConverter.convertEntity(STORAGE_SETTING_ID_STRING)).willReturn(STORAGE_SETTING_ID);
        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);
        given(uuidConverter.convertEntity(LOCATION_STRING)).willReturn(LOCATION);

        StorageSettingModel result = underTest.convertEntity(entity);

        assertThat(result.getId()).isEqualTo(STORAGE_SETTING_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.STORAGE_SETTING);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getLocationType()).isEqualTo(LOCATION_TYPE);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getTargetAmount()).isEqualTo(TARGET_AMOUNT);
        assertThat(result.getPriority()).isEqualTo(PRIORITY);
        assertThat(result.getBatchSize()).isEqualTo(BATCH_SIZE);
    }
}
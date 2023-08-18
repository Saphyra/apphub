package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.StorageSettingModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSetting;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting.StorageSettings;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StorageSettingLoaderTest {
    private static final UUID STORAGE_SETTING_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer TARGET_AMOUNT = 365;
    private static final Integer PRIORITY = 365;

    @Mock
    private GameItemLoader gameItemLoader;

    @InjectMocks
    private StorageSettingLoader underTest;

    @Mock
    private GameData gameData;

    @Mock
    private StorageSettings storageSettings;

    @Mock
    private StorageSetting storageSetting;

    @Mock
    private StorageSettingModel model;

    @Test
    void getGameItemType() {
        assertThat(underTest.getGameItemType()).isEqualTo(GameItemType.STORAGE_SETTING);
    }

    @Test
    void getArrayClass() {
        assertThat(underTest.getArrayClass()).isEqualTo(StorageSettingModel[].class);
    }

    @Test
    void addToGameData() {
        given(gameData.getStorageSettings()).willReturn(storageSettings);

        underTest.addToGameData(gameData, List.of(storageSetting));

        verify(storageSettings).addAll(List.of(storageSetting));
    }

    @Test
    void convert() {
        given(model.getId()).willReturn(STORAGE_SETTING_ID);
        given(model.getLocation()).willReturn(LOCATION);
        given(model.getDataId()).willReturn(DATA_ID);
        given(model.getTargetAmount()).willReturn(TARGET_AMOUNT);
        given(model.getPriority()).willReturn(PRIORITY);

        StorageSetting result = underTest.convert(model);

        assertThat(result.getStorageSettingId()).isEqualTo(STORAGE_SETTING_ID);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getTargetAmount()).isEqualTo(TARGET_AMOUNT);
        assertThat(result.getPriority()).isEqualTo(PRIORITY);
    }
}
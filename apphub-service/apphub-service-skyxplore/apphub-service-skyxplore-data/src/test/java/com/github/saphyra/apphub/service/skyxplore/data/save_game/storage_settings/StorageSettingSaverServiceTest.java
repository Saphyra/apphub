package com.github.saphyra.apphub.service.skyxplore.data.save_game.storage_settings;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.StorageSettingModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class StorageSettingSaverServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private StorageSettingDao storageSettingDao;

    @Mock
    private StorageSettingModelValidator storageSettingModelValidator;

    @InjectMocks
    private StorageSettingSaverService underTest;

    @Mock
    private StorageSettingModel model;

    @Test
    public void deleteByGameId() {
        underTest.deleteByGameId(GAME_ID);

        verify(storageSettingDao).deleteByGameId(GAME_ID);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.STORAGE_SETTING);
    }

    @Test
    public void save() {
        underTest.save(Arrays.asList(model));

        verify(storageSettingModelValidator).validate(model);
        verify(storageSettingDao).saveAll(Arrays.asList(model));
    }
}
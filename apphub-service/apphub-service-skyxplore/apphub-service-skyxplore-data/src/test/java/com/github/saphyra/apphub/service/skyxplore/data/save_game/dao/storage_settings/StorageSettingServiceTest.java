package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.storage_settings;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.StorageSettingModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StorageSettingServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID ID = UUID.randomUUID();

    @Mock
    private StorageSettingDao storageSettingDao;

    @Mock
    private StorageSettingModelValidator storageSettingModelValidator;

    @InjectMocks
    private StorageSettingService underTest;

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

    @Test
    public void findById() {
        given(storageSettingDao.findById(ID)).willReturn(Optional.of(model));

        Optional<StorageSettingModel> result = underTest.findById(ID);

        assertThat(result).contains(model);
    }

    @Test
    public void getByParent() {
        given(storageSettingDao.getByLocation(ID)).willReturn(Arrays.asList(model));

        List<StorageSettingModel> result = underTest.getByParent(ID);

        assertThat(result).containsExactly(model);
    }

    @Test
    public void deleteById() {
        underTest.deleteById(ID);

        verify(storageSettingDao).deleteById(ID);
    }
}
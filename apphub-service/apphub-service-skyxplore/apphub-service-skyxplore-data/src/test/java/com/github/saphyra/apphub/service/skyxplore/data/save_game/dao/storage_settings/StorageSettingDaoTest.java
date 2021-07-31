package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.storage_settings;

import com.github.saphyra.apphub.api.skyxplore.model.game.StorageSettingModel;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class StorageSettingDaoTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String GAME_ID_STRING = "game-id";
    private static final UUID STORAGE_SETTING_ID = UUID.randomUUID();
    private static final String STORAGE_SETTING_ID_STRING = "storage-setting-id";
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String LOCATION_STRING = "location";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private StorageSettingConverter converter;

    @Mock
    private StorageSettingRepository repository;

    @InjectMocks
    private StorageSettingDao underTest;

    @Mock
    private StorageSettingEntity entity;

    @Mock
    private StorageSettingModel model;

    @Test
    public void deleteByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);

        underTest.deleteByGameId(GAME_ID);

        verify(repository).deleteByGameId(GAME_ID_STRING);
    }

    @Test
    public void findById() {
        given(uuidConverter.convertDomain(STORAGE_SETTING_ID)).willReturn(STORAGE_SETTING_ID_STRING);
        given(repository.findById(STORAGE_SETTING_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(model));

        Optional<StorageSettingModel> result = underTest.findById(STORAGE_SETTING_ID);

        assertThat(result).contains(model);
    }

    @Test
    public void getByLocation() {
        given(uuidConverter.convertDomain(LOCATION)).willReturn(LOCATION_STRING);
        given(repository.getByLocation(LOCATION_STRING)).willReturn(Arrays.asList(entity));
        given(converter.convertEntity(Arrays.asList(entity))).willReturn(Arrays.asList(model));

        List<StorageSettingModel> result = underTest.getByLocation(LOCATION);

        assertThat(result).containsExactly(model);
    }

    @Test
    public void deleteById() {
        given(uuidConverter.convertDomain(STORAGE_SETTING_ID)).willReturn(STORAGE_SETTING_ID_STRING);
        given(repository.existsById(STORAGE_SETTING_ID_STRING)).willReturn(true);

        underTest.deleteById(STORAGE_SETTING_ID);

        verify(repository).deleteById(STORAGE_SETTING_ID_STRING);
    }
}
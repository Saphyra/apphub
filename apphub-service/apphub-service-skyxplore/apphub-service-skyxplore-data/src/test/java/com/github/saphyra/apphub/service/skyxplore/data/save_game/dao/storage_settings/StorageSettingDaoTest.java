package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.storage_settings;

import com.github.saphyra.apphub.api.skyxplore.model.game.StorageSettingModel;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StorageSettingDaoTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String GAME_ID_STRING = "game-id";
    private static final UUID STORAGE_SETTING_ID = UUID.randomUUID();
    private static final String STORAGE_SETTING_ID_STRING = "storage-setting-id";
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String LOCATION_STRING = "location";
    private static final int PAGE = 254;
    private static final int ITEMS_PER_PAGE = 346;

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

    @Test
    void getPageByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(repository.getByGameId(GAME_ID_STRING, PageRequest.of(PAGE, ITEMS_PER_PAGE))).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(model));

        assertThat(underTest.getPageByGameId(GAME_ID, PAGE, ITEMS_PER_PAGE)).containsExactly(model);
    }
}
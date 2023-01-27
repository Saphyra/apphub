package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class StorageSettingsTest {
    private static final String DATA_ID = "data-id";
    private static final UUID STORAGE_SETTING_ID = UUID.randomUUID();

    @Mock
    private StorageSetting storageSetting;

    @Test
    public void findByDataId() {
        given(storageSetting.getDataId()).willReturn(DATA_ID);

        StorageSettings underTest = new StorageSettings(List.of(storageSetting));

        Optional<StorageSetting> result = underTest.findByDataId(DATA_ID);

        assertThat(result).contains(storageSetting);
    }

    @Test
    public void deleteByStorageSettingId() {
        given(storageSetting.getStorageSettingId()).willReturn(STORAGE_SETTING_ID);

        StorageSettings underTest = new StorageSettings(List.of(storageSetting));

        underTest.deleteByStorageSettingId(STORAGE_SETTING_ID);

        assertThat(underTest).isEmpty();
    }

    @Test
    public void findByStorageSettingId() {
        given(storageSetting.getStorageSettingId()).willReturn(STORAGE_SETTING_ID);

        StorageSettings underTest = new StorageSettings(List.of(storageSetting));

        Optional<StorageSetting> result = underTest.findByStorageSettingId(STORAGE_SETTING_ID);

        assertThat(result).contains(storageSetting);
    }

    @Test
    public void findByStorageSettingIdValidated_notFound() {
        StorageSettings underTest = new StorageSettings();

        Throwable ex = catchThrowable(() -> underTest.findByStorageSettingIdValidated(STORAGE_SETTING_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }
}
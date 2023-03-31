package com.github.saphyra.apphub.service.skyxplore.game.domain.data.storage_setting;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StorageSettingsTest {
    private static final UUID STORAGE_SETTING_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String DATA_ID = "data-id";

    private final StorageSettings underTest = new StorageSettings();

    @Mock
    private StorageSetting storageSetting1;

    @Mock
    private StorageSetting storageSetting2;

    @Test
    void findByStorageSettingIdValidated_found() {
        given(storageSetting1.getStorageSettingId()).willReturn(STORAGE_SETTING_ID);

        underTest.add(storageSetting1);

        assertThat(underTest.findByStorageSettingIdValidated(STORAGE_SETTING_ID)).isEqualTo(storageSetting1);
    }

    @Test
    void findByStorageSettingIdValidated_notFound() {
        given(storageSetting1.getStorageSettingId()).willReturn(UUID.randomUUID());

        underTest.add(storageSetting1);

        Throwable ex = catchThrowable(() -> underTest.findByStorageSettingIdValidated(STORAGE_SETTING_ID));

        ExceptionValidator.validateLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void getByLocation() {
        given(storageSetting1.getLocation()).willReturn(LOCATION);
        given(storageSetting2.getLocation()).willReturn(UUID.randomUUID());

        underTest.addAll(List.of(storageSetting1, storageSetting2));

        assertThat(underTest.getByLocation(LOCATION)).containsExactly(storageSetting1);
    }

    @Test
    void findByLocationAndDataId_found() {
        given(storageSetting1.getLocation()).willReturn(LOCATION);
        given(storageSetting1.getDataId()).willReturn(DATA_ID);

        underTest.add(storageSetting1);

        assertThat(underTest.findByLocationAndDataId(LOCATION, DATA_ID)).contains(storageSetting1);
    }

    @Test
    void findByLocationAndDataId_notFound() {
        given(storageSetting1.getLocation()).willReturn(UUID.randomUUID());

        given(storageSetting2.getLocation()).willReturn(LOCATION);
        given(storageSetting2.getDataId()).willReturn("asd");

        underTest.addAll(List.of(storageSetting1, storageSetting2));

        assertThat(underTest.findByLocationAndDataId(LOCATION, DATA_ID)).isEmpty();
    }
}
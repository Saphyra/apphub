package com.github.saphyra.apphub.service.user.settings;

import com.github.saphyra.apphub.api.user.model.request.SetUserSettingsRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.user.settings.dao.UserSetting;
import com.github.saphyra.apphub.service.user.settings.dao.UserSettingDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserSettingsControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String CATEGORY = "category";
    private static final String KEY = "key";
    private static final String DEFAULT_KEY = "default-key";
    private static final String VALUE = "value";
    private static final String DEFAULT_VALUE = "default-value";

    @Mock
    private UserSettingDao userSettingDao;

    @Mock
    private UserSettingProperties properties;

    @InjectMocks
    private UserSettingsControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private UserSetting userSetting;

    @BeforeEach
    public void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void getUserSettings_categoryNotFound() {
        given(properties.getSettings()).willReturn(Collections.emptyMap());

        Throwable ex = catchThrowable(() -> underTest.getUserSettings(CATEGORY, accessTokenHeader));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    public void getUserSettings() {
        given(properties.getSettings()).willReturn(CollectionUtils.toMap(
            CATEGORY,
            CollectionUtils.toMap(
                new BiWrapper<>(KEY, "asd"),
                new BiWrapper<>(DEFAULT_KEY, DEFAULT_VALUE)
            )
        ));

        given(userSettingDao.getByUserIdAndCategory(USER_ID, CATEGORY)).willReturn(List.of(userSetting));
        given(userSetting.getKey()).willReturn(KEY);
        given(userSetting.getValue()).willReturn(VALUE);

        Map<String, String> result = underTest.getUserSettings(CATEGORY, accessTokenHeader);

        assertThat(result).hasSize(2);
        assertThat(result).containsEntry(KEY, VALUE);
        assertThat(result).containsEntry(DEFAULT_KEY, DEFAULT_VALUE);
    }

    @Test
    public void setUserSettings_blankKey() {
        SetUserSettingsRequest request = SetUserSettingsRequest.builder()
            .category(CATEGORY)
            .key(" ")
            .value(VALUE)
            .build();

        Throwable ex = catchThrowable(() -> underTest.setUserSettings(request, accessTokenHeader));

        ExceptionValidator.validateInvalidParam(ex, "key", "must not be null or blank");
    }

    @Test
    public void setUserSettings_categoryNotFound() {
        SetUserSettingsRequest request = SetUserSettingsRequest.builder()
            .category("asd")
            .key(KEY)
            .value(VALUE)
            .build();

        Throwable ex = catchThrowable(() -> underTest.setUserSettings(request, accessTokenHeader));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    public void setUserSettings() {
        SetUserSettingsRequest request = SetUserSettingsRequest.builder()
            .category(CATEGORY)
            .key(KEY)
            .value(VALUE)
            .build();

        given(properties.getSettings()).willReturn(CollectionUtils.toMap(
            CATEGORY,
            CollectionUtils.toMap(
                new BiWrapper<>(KEY, "asd"),
                new BiWrapper<>(DEFAULT_KEY, DEFAULT_VALUE)
            )
        ));

        underTest.setUserSettings(request, accessTokenHeader);

        ArgumentCaptor<UserSetting> argumentCaptor = ArgumentCaptor.forClass(UserSetting.class);
        verify(userSettingDao).save(argumentCaptor.capture());
        UserSetting userSetting = argumentCaptor.getValue();

        assertThat(userSetting.getUserId()).isEqualTo(USER_ID);
        assertThat(userSetting.getCategory()).isEqualTo(CATEGORY);
        assertThat(userSetting.getKey()).isEqualTo(KEY);
        assertThat(userSetting.getValue()).isEqualTo(VALUE);
    }
}
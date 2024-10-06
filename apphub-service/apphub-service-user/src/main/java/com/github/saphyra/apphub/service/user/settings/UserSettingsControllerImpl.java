package com.github.saphyra.apphub.service.user.settings;

import com.github.saphyra.apphub.api.user.model.SetUserSettingsRequest;
import com.github.saphyra.apphub.api.user.server.UserSettingsController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.user.settings.dao.UserSetting;
import com.github.saphyra.apphub.service.user.settings.dao.UserSettingDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserSettingsControllerImpl implements UserSettingsController {
    private final UserSettingDao userSettingDao;
    private final UserSettingProperties properties;

    @Override
    public Map<String, String> getUserSettings(String category, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know his settings for category {}", accessTokenHeader.getUserId(), category);

        Map<String, String> defaults = getDefaults(category);

        Map<String, String> existingSettings = userSettingDao.getByUserIdAndCategory(accessTokenHeader.getUserId(), category)
            .stream()
            .collect(Collectors.toMap(UserSetting::getKey, UserSetting::getValue));

        return defaults.entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> existingSettings.getOrDefault(entry.getKey(), entry.getValue())));
    }

    @Override
    public Map<String, String> setUserSettings(SetUserSettingsRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to modify his settings of category {}", accessTokenHeader.getUserId(), request.getCategory());

        ValidationUtil.notBlank(request.getKey(), "key");

        Map<String, String> defaults = getDefaults(request.getCategory());

        if (!defaults.containsKey(request.getKey())) {
            throw ExceptionFactory.invalidParam("key", "not supported");
        }

        UserSetting setting = UserSetting.builder()
            .userId(accessTokenHeader.getUserId())
            .category(request.getCategory())
            .key(request.getKey())
            .value(request.getValue())
            .build();

        userSettingDao.save(setting);

        return getUserSettings(request.getCategory(), accessTokenHeader);
    }

    private Map<String, String> getDefaults(String category) {
        Map<String, String> defaults = properties.getSettings()
            .get(category);

        if (isNull(defaults)) {
            throw ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Category not found with name " + category);
        }
        return defaults;
    }
}

package com.github.saphyra.apphub.service.feature.skyxplore.data.setting;

import com.github.saphyra.apphub.api.feature.skyxplore.data.server.SkyXploreSettingsController;
import com.github.saphyra.apphub.api.feature.skyxplore.model.data.setting.SettingIdentifier;
import com.github.saphyra.apphub.api.feature.skyxplore.model.data.setting.SettingModel;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.feature.skyxplore.data.setting.service.CreateOrUpdateSettingService;
import com.github.saphyra.apphub.service.feature.skyxplore.data.setting.service.DeleteSettingService;
import com.github.saphyra.apphub.service.feature.skyxplore.data.setting.service.SettingQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
class SkyXploreDataSettingControllerImpl implements SkyXploreSettingsController {
    private final CreateOrUpdateSettingService createOrUpdateSettingService;
    private final SettingQueryService settingQueryService;
    private final DeleteSettingService deleteSettingService;

    @Override
    public void createOrUpdateSetting(SettingModel request, AccessToken accessToken) {
        log.info("{} wants to save a setting with type {} at location {}", accessToken.getUserId(), request.getType(), request.getLocation());

        createOrUpdateSettingService.createOrUpdate(accessToken.getUserId(), request);
    }

    @Override
    public OneParamResponse<SettingModel> getSetting(SettingIdentifier request, AccessToken accessToken) {
        log.info("{} wants to get a setting by {}", accessToken.getUserId(), request);
        return new OneParamResponse<>(settingQueryService.getSetting(accessToken.getUserId(), request));
    }

    @Override
    public OneParamResponse<SettingModel> deleteSetting(SettingIdentifier request, AccessToken accessToken) {
        log.info("{} wants to delete setting {}", accessToken.getUserId(), request);
        deleteSettingService.delete(accessToken.getUserId(), request);

        return getSetting(request, accessToken);
    }
}

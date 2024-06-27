package com.github.saphyra.apphub.service.skyxplore.data.setting;

import com.github.saphyra.apphub.api.skyxplore.data.server.SkyXploreSettingsController;
import com.github.saphyra.apphub.api.skyxplore.model.data.setting.SettingIdentifier;
import com.github.saphyra.apphub.api.skyxplore.model.data.setting.SettingModel;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.skyxplore.data.setting.service.CreateOrUpdateSettingService;
import com.github.saphyra.apphub.service.skyxplore.data.setting.service.DeleteSettingService;
import com.github.saphyra.apphub.service.skyxplore.data.setting.service.SettingQueryService;
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
    public void createOrUpdateSetting(SettingModel request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to save a setting with type {} at location {}", accessTokenHeader.getUserId(), request.getType(), request.getLocation());

        createOrUpdateSettingService.createOrUpdate(accessTokenHeader.getUserId(), request);
    }

    @Override
    public OneParamResponse<SettingModel> getSetting(SettingIdentifier request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to get a setting by {}", accessTokenHeader.getUserId(), request);
        return new OneParamResponse<>(settingQueryService.getSetting(accessTokenHeader.getUserId(), request));
    }

    @Override
    public OneParamResponse<SettingModel> deleteSetting(SettingIdentifier request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete setting {}", accessTokenHeader.getUserId(), request);
        deleteSettingService.delete(accessTokenHeader.getUserId(), request);

        return getSetting(request, accessTokenHeader);
    }
}

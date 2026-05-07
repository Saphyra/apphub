package com.github.saphyra.apphub.api.feature.skyxplore.data.server;

import com.github.saphyra.apphub.api.feature.skyxplore.model.data.setting.SettingIdentifier;
import com.github.saphyra.apphub.api.feature.skyxplore.model.data.setting.SettingModel;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.api.feature.skyxplore.SkyXploreDataEndpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

public interface SkyXploreSettingsController {
    @PutMapping(SkyXploreDataEndpoints.SKYXPLORE_DATA_CREATE_SETTING)
    void createOrUpdateSetting(@RequestBody SettingModel request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessToken accessToken);

    @PostMapping(SkyXploreDataEndpoints.SKYXPLORE_DATA_GET_SETTING)
    OneParamResponse<SettingModel> getSetting(@RequestBody SettingIdentifier request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessToken accessToken);

    @DeleteMapping(SkyXploreDataEndpoints.SKYXPLORE_DATA_DELETE_SETTING)
    OneParamResponse<SettingModel> deleteSetting(@RequestBody SettingIdentifier request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessToken accessToken);
}

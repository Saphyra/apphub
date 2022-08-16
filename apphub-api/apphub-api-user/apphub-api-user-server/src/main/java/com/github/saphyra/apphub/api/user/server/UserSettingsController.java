package com.github.saphyra.apphub.api.user.server;

import com.github.saphyra.apphub.api.user.model.request.SetUserSettingsRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

public interface UserSettingsController {
    @GetMapping(Endpoints.GET_USER_SETTINGS)
    Map<String, String> getUserSettings(@PathVariable("category") String category, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.SET_USER_SETTINGS)
    void setUserSettings(@RequestBody SetUserSettingsRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}

package com.github.saphyra.apphub.api.user.server;

import com.github.saphyra.apphub.api.user.model.request.SetUserSettingsRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

public interface UserSettingsController {
    /**
     * Searching for user settings of the given category, falling back to default values if the user has no settings stored in the database.
     */
    @GetMapping(Endpoints.GET_USER_SETTINGS)
    Map<String, String> getUserSettings(@PathVariable("category") String category, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    /**
     * Saving the setting to the database after checking if the given key is applicable for the given category.
     */
    @PostMapping(Endpoints.SET_USER_SETTINGS)
    Map<String, String> setUserSettings(@RequestBody SetUserSettingsRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}

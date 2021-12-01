package com.github.saphyra.apphub.api.user.server;

import com.github.saphyra.apphub.api.user.model.request.BanRequest;
import com.github.saphyra.apphub.api.user.model.response.BanResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

public interface BanController {
    @PostMapping(Endpoints.ACCOUNT_BAN_USER)
    void banUser(@RequestBody BanRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(Endpoints.ACCOUNT_REMOVE_BAN)
    void revokeBan(@RequestBody OneParamRequest<String> password, @PathVariable("banId") UUID banId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(Endpoints.ACCOUNT_GET_BANS)
    List<BanResponse> getBans(@PathVariable("userId") UUID bannedUserId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}

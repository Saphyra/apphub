package com.github.saphyra.apphub.api.user.client;

import com.github.saphyra.apphub.api.user.model.ban.BanResponse;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.endpoints.UserEndpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "user-ban", url = "${serviceUrls.user}")
@Deprecated(forRemoval = true)
public interface BanClient {
    @GetMapping(UserEndpoints.ACCOUNT_GET_BANS)
    BanResponse getBans(@PathVariable("userId") UUID bannedUserId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) String accessTokenHeader, @RequestHeader(Constants.LOCALE_HEADER) String locale);
}

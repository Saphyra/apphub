package com.github.saphyra.apphub.api.etc.user.client;

import com.github.saphyra.apphub.api.etc.user.model.UserEndpoints;
import com.github.saphyra.apphub.api.etc.user.model.authorization.AuthorizationRequest;
import com.github.saphyra.apphub.api.etc.user.model.authorization.AuthorizationResponse;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "user-authorization", url = "${serviceUrls.user}")
public interface AuthorizationClient {
    @PostMapping(UserEndpoints.INTERNAL_AUTHORIZATION_AUTHORIZE)
    AuthorizationResponse authorize(@RequestBody AuthorizationRequest request, @RequestHeader(Constants.LOCALE_HEADER) String locale);

    @GetMapping(UserEndpoints.INTERNAL_AUTHORIZATION_GET_ROLES)
    List<String> getRoles(@PathVariable("userId") UUID userId,  @RequestHeader(Constants.LOCALE_HEADER) String locale);
}

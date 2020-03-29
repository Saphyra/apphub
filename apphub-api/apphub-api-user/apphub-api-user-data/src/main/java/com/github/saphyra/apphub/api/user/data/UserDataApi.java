package com.github.saphyra.apphub.api.user.data;

import com.github.saphyra.apphub.api.user.data.request.RegistrationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient
public interface UserDataApi {
    @RequestMapping(method = RequestMethod.POST, value = "/api/user/data")
    void register(@RequestBody RegistrationRequest registrationRequest);
}

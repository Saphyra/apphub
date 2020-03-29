package com.github.saphyra.apphub.service.user.data;

import com.github.saphyra.apphub.api.user.data.UserDataApi;
import com.github.saphyra.apphub.api.user.data.request.RegistrationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
//TODO unit test
//TODO int test
//TODO api test
//TODO fe test
public class UserDataController implements UserDataApi {
    @Override
    public void register(RegistrationRequest registrationRequest) {
        log.info("RegistrationRequest arrived for username {} and email {}", registrationRequest.getUsername(), registrationRequest.getEmail());
    }
}

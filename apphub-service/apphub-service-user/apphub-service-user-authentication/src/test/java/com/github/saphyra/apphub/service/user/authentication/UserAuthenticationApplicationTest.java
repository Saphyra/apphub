package com.github.saphyra.apphub.service.user.authentication;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserAuthenticationApplication.class)
@ActiveProfiles("test")
public class UserAuthenticationApplicationTest {
    @Test
    public void startup() {

    }
}
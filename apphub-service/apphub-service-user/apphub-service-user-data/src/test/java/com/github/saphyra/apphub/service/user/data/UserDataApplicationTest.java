package com.github.saphyra.apphub.service.user.data;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserDataApplication.class)
@ActiveProfiles("test")
public class UserDataApplicationTest {
    @Test
    public void startup() {

    }
}
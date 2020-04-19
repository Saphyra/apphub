package com.github.saphyra.apphub.service.platform.scheduler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SchedulerApplication.class)
@ActiveProfiles("test")
public class SchedulerApplicationTest {
    @Test
    public void startup() {

    }
}
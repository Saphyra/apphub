package com.github.saphyra.apphub.service.platform.event_gateway;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EventGatewayApplication.class)
@ActiveProfiles("test")
public class EventGatewayApplicationTest {
    @Test
    public void startup() {

    }
}
package com.github.saphyra.apphub.service.platform.scheduler;

import com.github.saphyra.apphub.lib.config.common.GenericEndpoints;
import com.github.saphyra.apphub.test.rest_assured.RequestFactory;
import com.github.saphyra.apphub.test.rest_assured.UrlFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class SchedulerApplicationTest {
    @LocalServerPort
    private int serverPort;

    @Test
    public void startup() {
        int statusCode = RequestFactory.createRequest()
            .get(UrlFactory.create(serverPort, GenericEndpoints.HEALTH))
            .getStatusCode();

        assertThat(statusCode).isEqualTo(HttpStatus.OK.value());
    }
}
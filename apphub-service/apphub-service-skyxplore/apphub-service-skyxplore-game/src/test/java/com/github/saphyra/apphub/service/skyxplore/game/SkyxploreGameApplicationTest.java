package com.github.saphyra.apphub.service.skyxplore.game;

import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.test.common.rest_assured.RequestFactory;
import com.github.saphyra.apphub.test.common.rest_assured.UrlFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class SkyxploreGameApplicationTest {
    @LocalServerPort
    private int serverPort;

    @Test
    public void startup() {
        int statusCode = RequestFactory.createRequest()
            .get(UrlFactory.create(serverPort, Endpoints.HEALTH))
            .getStatusCode();

        assertThat(statusCode).isEqualTo(HttpStatus.OK.value());
    }
}
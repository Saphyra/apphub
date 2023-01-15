package com.github.saphyra.apphub.service.admin_panel;

import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.test.common.rest_assured.RequestFactory;
import com.github.saphyra.apphub.test.common.rest_assured.UrlFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AdminPanelApplicationTest {
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
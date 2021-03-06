package com.github.saphyra.apphub.service.notebook;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.test.common.rest_assured.RequestFactory;
import com.github.saphyra.apphub.test.common.rest_assured.UrlFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = "event.processor.enabled=true")
public class NotebookApplicationTest {
    @LocalServerPort
    private int serverPort;

    @MockBean
    private EventGatewayApiClient eventGatewayApiClient;

    @Test
    public void startup() {
        int statusCode = RequestFactory.createRequest()
            .get(UrlFactory.create(serverPort, Endpoints.HEALTH))
            .getStatusCode();

        assertThat(statusCode).isEqualTo(HttpStatus.OK.value());

        verify(eventGatewayApiClient).registerProcessor(any());
    }
}
package com.github.saphyra.apphub.service.utils.log_formatter;

import com.github.saphyra.apphub.api.utils.model.request.SetLogParameterVisibilityRequest;
import com.github.saphyra.apphub.api.utils.model.response.LogParameterVisibilityResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.converter.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.service.utils.log_formatter.repository.LogParameterVisibilityDao;
import com.github.saphyra.apphub.test.common.api.ApiTestConfiguration;
import com.github.saphyra.apphub.test.common.rest_assured.RequestFactory;
import com.github.saphyra.apphub.test.common.rest_assured.UrlFactory;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = ApiTestConfiguration.class)
public class LogFormatterControllerImplTestIt {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final AccessTokenHeader ACCESS_TOKEN_HEADER = AccessTokenHeader.builder()
        .accessTokenId(UUID.randomUUID())
        .userId(USER_ID)
        .roles(Arrays.asList("ACCESS", "UTILS"))
        .build();
    private static final String PARAMETER = "parameter";

    @LocalServerPort
    private int serverPort;

    @Autowired
    private AccessTokenHeaderConverter accessTokenHeaderConverter;

    @Autowired
    private LogParameterVisibilityDao dao;

    @AfterEach
    public void clear() {
        dao.deleteAll();
    }

    @Test
    public void crud() {
        Response createResponse = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(Arrays.asList(PARAMETER))
            .put(UrlFactory.create(serverPort, Endpoints.UTILS_LOG_FORMATTER_GET_VISIBILITY));

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        LogParameterVisibilityResponse[] createData = createResponse.getBody().as(LogParameterVisibilityResponse[].class);

        assertThat(createData).hasSize(1);
        assertThat(createData[0].getParameter()).isEqualTo(PARAMETER);
        assertThat(createData[0].isVisibility()).isTrue();

        UUID id = createData[0].getId();

        Response editResponse = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(SetLogParameterVisibilityRequest.builder().id(id).visible(false).build())
            .post(UrlFactory.create(serverPort, Endpoints.UTILS_LOG_FORMATTER_SET_VISIBILITY));

        assertThat(editResponse.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        Response queryResponse = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(Arrays.asList(PARAMETER))
            .put(UrlFactory.create(serverPort, Endpoints.UTILS_LOG_FORMATTER_GET_VISIBILITY));

        assertThat(queryResponse.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        LogParameterVisibilityResponse[] queryData = queryResponse.getBody().as(LogParameterVisibilityResponse[].class);

        assertThat(queryData).hasSize(1);
        assertThat(queryData[0].getParameter()).isEqualTo(PARAMETER);
        assertThat(queryData[0].isVisibility()).isFalse();
    }
}
package com.github.saphyra.apphub.integration.core.integration_server;

import com.github.saphyra.apphub.integration.core.TestConfiguration;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.IntegrationServerEndpoints;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import io.restassured.config.DecoderConfig;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.http.params.CoreConnectionPNames;

import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

class IntegrationServerApi {
    static UUID createTestRun() {
        Response response = createRequest()
            .put(UrlFactory.create(TestConfiguration.INTEGRATION_SERVER_PORT, IntegrationServerEndpoints.INTEGRATION_SERVER_CREATE_TEST_RUN));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(UUID.class);
    }

    static void completeTestRun(UUID testRunId, String status) {
        Response response = createRequest()
            .body(new OneParamRequest<>(status))
            .post(UrlFactory.create(TestConfiguration.INTEGRATION_SERVER_PORT, IntegrationServerEndpoints.INTEGRATION_SERVER_FINISH_TEST_RUN, "testRunId", testRunId));

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    static void reportTestCaseRun(UUID testRunId, String methodIdentifier, String testCaseName, List<String> groups, long duration, String status) {
        ReportTestCaseRequest request = ReportTestCaseRequest.builder()
            .testCase(TestCaseRequest.builder()
                .id(methodIdentifier)
                .name(testCaseName)
                .groups(groups)
                .build())
            .testCaseRun(TestCaseRunRequest.builder()
                .testCaseId(methodIdentifier)
                .duration(duration)
                .status(status)
                .build())
            .build();

        Response response = createRequest()
            .body(request)
            .put(UrlFactory.create(TestConfiguration.INTEGRATION_SERVER_PORT, IntegrationServerEndpoints.INTEGRATION_SERVER_REPORT_TEST_CASE, "testRunId", testRunId));

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    static long getAverageDuration(String methodIdentifier) {
        Response response = createRequest()
            .body(new OneParamRequest<>(methodIdentifier))
            .post(UrlFactory.create(TestConfiguration.INTEGRATION_SERVER_PORT, IntegrationServerEndpoints.INTEGRATION_SERVER_GET_AVERAGE_RUN_TIME));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(Long.class);
    }

    static RequestSpecification createRequest() {
        RequestSpecification requestSpecification = given()
            .config(
                RestAssuredConfig.config()
                    .decoderConfig(DecoderConfig.decoderConfig().contentDecoders(DecoderConfig.ContentDecoder.DEFLATE))
                    .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000)
                        .setParam(CoreConnectionPNames.SO_TIMEOUT, 10000)
                    )
            )
            .contentType(ContentType.JSON)
            .header("Connection", "close");

        if (TestConfiguration.REST_LOGGING_ENABLED) {
            requestSpecification.filter(new ResponseLoggingFilter()).log().all();
        }
        return requestSpecification;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    private static class ReportTestCaseRequest {
        private TestCaseRequest testCase;
        private TestCaseRunRequest testCaseRun;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    private static class TestCaseRequest {
        private String id;
        private String name;
        private List<String> groups;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    private static class TestCaseRunRequest {
        private String testCaseId;
        private Long duration;
        private String status;
    }
}

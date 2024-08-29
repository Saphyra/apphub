package com.github.saphyra.apphub.integration.framework;

import io.restassured.response.Response;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

public class CommonUtils {
    public static String withLeadingZeros(int number, int length) {
        StringBuilder num = new StringBuilder(String.valueOf(number));

        while (num.length() < length) {
            num.insert(0, "0");
        }

        return num.toString();
    }

    public static void verifyMissingRole(Supplier<Response> apiCall) {
        ResponseValidator.verifyErrorResponse(apiCall.get(), 403, ErrorCode.MISSING_ROLE);
    }

    public static void verifyMissingRole(int serverPort, WebDriver driver, String page) {
        driver.navigate().to(UrlFactory.create(serverPort, page));

        verifyMissingRole(serverPort, driver.getCurrentUrl());
    }

    public static void verifyMissingRole(int serverPort, String uri) {
        ParsedUri parsedUri = parseUri(uri);

        assertThat(parsedUri.getUri()).isEqualTo(UrlFactory.create(serverPort, Endpoints.ERROR_PAGE));
        assertThat(parsedUri.getQueryParams()).containsEntry("error_code", ErrorCode.MISSING_ROLE.name());
    }

    public static ParsedUri parseUri(String uri) {
        String[] split = uri.split("\\?");
        String url = split[0];
        Map<String, String> queryParams = new HashMap<>();
        if (split.length > 1) {
            parseQueryParams(queryParams, split[1]);
        }

        return new ParsedUri(url, queryParams);
    }

    private static void parseQueryParams(Map<String, String> parsed, String queryParamString) {
        String[] pairs = queryParamString.split("&");
        for (String pair : pairs) {
            String[] kp = pair.split("=");
            parsed.put(kp[0], kp[1]);
        }
    }
}

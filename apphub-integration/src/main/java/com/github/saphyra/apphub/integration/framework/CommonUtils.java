package com.github.saphyra.apphub.integration.framework;

import com.github.saphyra.apphub.integration.framework.endpoints.GenericEndpoints;
import io.restassured.response.Response;
import org.openqa.selenium.JavascriptExecutor;
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

    /**
     * Verifies missing role of an already assembled URL
     */
    public static void verifyMissingRole(WebDriver driver, String uri, int serverPort) {
        driver.navigate().to(uri);

        verifyMissingRole(serverPort, driver.getCurrentUrl());
    }

    /**
     * Verifies missing role of a page
     */
    public static void verifyMissingRole(int serverPort, WebDriver driver, String page) {
        verifyMissingRole(serverPort, driver, page, Map.of());
    }

    public static void verifyMissingRole(int serverPort, WebDriver driver, String page, Map<String, Object> pathVariables) {
        driver.navigate().to(UrlFactory.create(serverPort, page, pathVariables));

        AwaitilityWrapper.awaitAssert(() -> verifyMissingRole(serverPort, driver.getCurrentUrl()));
    }

    public static void verifyMissingRole(int serverPort, String uri) {
        ParsedUri parsedUri = parseUri(uri);

        assertThat(parsedUri.getUri()).isEqualTo(UrlFactory.create(serverPort, GenericEndpoints.ERROR_PAGE));
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

    public static void enableTestMode(WebDriver driver) {
        ((JavascriptExecutor) driver).executeScript("enableTestMode()");

        WebElementUtils.waitForSpinnerToDisappear(driver);
    }
}

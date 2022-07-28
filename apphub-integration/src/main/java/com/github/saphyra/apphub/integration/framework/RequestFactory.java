package com.github.saphyra.apphub.integration.framework;

import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.core.TestBase;
import io.restassured.config.DecoderConfig;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.apache.http.params.CoreConnectionPNames;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static java.util.Objects.isNull;

public class RequestFactory {
    public static RequestSpecification createAuthorizedRequest(Language locale, UUID accessTokenId) {
        return createRequest(locale)
            .cookie(Constants.ACCESS_TOKEN_COOKIE, accessTokenId);
    }

    public static RequestSpecification createRequest(Language locale) {
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
            .header("Connection", "close")
            .header("Request-Type", "rest");
        if (!isNull(locale)) {
            requestSpecification.cookie(Constants.LOCALE_COOKIE, locale.getLocale());
        }

        if (TestBase.REST_LOGGING_ENABLED) {
            requestSpecification.filter(new ResponseLoggingFilter())
                .log().all();
        }
        return requestSpecification;
    }
}

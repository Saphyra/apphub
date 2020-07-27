package com.github.saphyra.apphub.integration.common.framework;

import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import io.restassured.config.DecoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

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
            .config(RestAssuredConfig.config().decoderConfig(DecoderConfig.decoderConfig().contentDecoders(DecoderConfig.ContentDecoder.DEFLATE)))
            //.filter(new ResponseLoggingFilter())
            //.log().all()
            .contentType(ContentType.JSON)
            .header("Connection", "close")
            .header("Request-Type", "rest");
        if (!isNull(locale)) {
            requestSpecification.cookie(Constants.LOCALE_COOKIE, locale.getLocale());
        }
        return requestSpecification;
    }
}

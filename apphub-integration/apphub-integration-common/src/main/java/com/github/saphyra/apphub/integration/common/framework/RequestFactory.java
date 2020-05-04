package com.github.saphyra.apphub.integration.common.framework;

import io.restassured.config.DecoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.UUID;

import static io.restassured.RestAssured.given;

public class RequestFactory {
    public static RequestSpecification createRequest() {
        return given()
            .config(RestAssuredConfig.config().decoderConfig(DecoderConfig.decoderConfig().contentDecoders(DecoderConfig.ContentDecoder.DEFLATE)))
            .filter(new ResponseLoggingFilter())
            .log().all()
            .contentType(ContentType.JSON)
            .header(Constants.LOCALE_COOKIE, "hu");
    }

    public static RequestSpecification createAuthorizedRequest(UUID accessTokenId) {
        return createRequest()
            .cookie(Constants.ACCESS_TOKEN_COOKIE, accessTokenId);
    }
}

package com.github.saphyra.apphub.test.rest_assured;

import com.github.saphyra.apphub.lib.common_domain.Constants;
import io.restassured.config.DecoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class RequestFactory {
    public static RequestSpecification createAuthorizedRequest(String accessToken) {
        return createRequest()
            .header(Constants.ACCESS_TOKEN_HEADER, accessToken);
    }

    public static RequestSpecification createRequest() {
        return given()
            .config(RestAssuredConfig.config().decoderConfig(DecoderConfig.decoderConfig().contentDecoders(DecoderConfig.ContentDecoder.DEFLATE)))
            .filter(new ResponseLoggingFilter())
            .log().all()
            .contentType(ContentType.JSON);
    }
}

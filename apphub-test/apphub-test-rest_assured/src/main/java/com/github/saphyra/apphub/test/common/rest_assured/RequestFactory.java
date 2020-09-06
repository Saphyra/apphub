package com.github.saphyra.apphub.test.common.rest_assured;

import com.github.saphyra.apphub.lib.common_util.Constants;
import io.restassured.config.DecoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static com.github.saphyra.apphub.test.common.TestConstants.DEFAULT_LOCALE;
import static io.restassured.RestAssured.given;

public class RequestFactory {
    public static RequestSpecification createAuthorizedRequest(String headerValue) {
        return createRequest()
            .header(Constants.ACCESS_TOKEN_HEADER, headerValue);
    }

    public static RequestSpecification createRequest(String locale){
        return given()
            .config(RestAssuredConfig.config().decoderConfig(DecoderConfig.decoderConfig().contentDecoders(DecoderConfig.ContentDecoder.DEFLATE)))
            //.filter(new ResponseLoggingFilter())
            //.log().all()
            .contentType(ContentType.JSON)
            .header(Constants.LOCALE_COOKIE, locale);
    }

    public static RequestSpecification createRequest() {
        return createRequest(DEFAULT_LOCALE);
    }
}

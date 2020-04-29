package com.github.saphyra.apphub.test.common.rest_assured;

import com.github.saphyra.apphub.lib.common_util.Constants;
import io.restassured.config.DecoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class RequestFactory {
    public static RequestSpecification createRequest() {
        return given()
            .config(RestAssuredConfig.config().decoderConfig(DecoderConfig.decoderConfig().contentDecoders(DecoderConfig.ContentDecoder.DEFLATE)))
            //.filter(new ResponseLoggingFilter())
            //.log().all()
            .contentType(ContentType.JSON)
            .header(Constants.LOCALE_COOKIE, "hu");
    }
}

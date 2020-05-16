package com.github.saphyra.apphub.integration.common.framework;

import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import io.restassured.config.DecoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.UUID;

import static io.restassured.RestAssured.given;

public class RequestFactory {
    public static RequestSpecification createAuthorizedRequest(Language locale, UUID accessTokenId) {
        return createRequest(locale)
            .cookie(Constants.ACCESS_TOKEN_COOKIE, accessTokenId);
    }
    
    public static RequestSpecification createRequest(Language locale) {
        return given()
            .config(RestAssuredConfig.config().decoderConfig(DecoderConfig.decoderConfig().contentDecoders(DecoderConfig.ContentDecoder.DEFLATE)))
            .filter(new ResponseLoggingFilter())
            .log().all()
            .contentType(ContentType.JSON)
            .cookie(Constants.LOCALE_COOKIE, locale.getLocale());
    }
}

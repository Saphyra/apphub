package com.github.saphyra.apphub.integration.framework;

import io.restassured.response.Response;

import java.util.function.Supplier;

public class CommonUtils {
    public static String withLeadingZeros(int number, int length) {
        String num = String.valueOf(number);

        while (num.length() < length) {
            num = "0" + num;
        }

        return num;
    }

    public static void verifyMissingRole(Supplier<Response> apiCall){
        ResponseValidator.verifyErrorResponse(apiCall.get(), 403, ErrorCode.MISSING_ROLE);
    }
}

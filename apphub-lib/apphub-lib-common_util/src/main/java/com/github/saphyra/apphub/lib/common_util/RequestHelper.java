package com.github.saphyra.apphub.lib.common_util;

import lombok.experimental.UtilityClass;

import javax.servlet.http.HttpServletRequest;

@UtilityClass
public class RequestHelper {
    public boolean isRestCall(HttpServletRequest request){
        String header = request.getHeader(Constants.REQUEST_TYPE_HEADER);
        return Constants.REQUEST_TYPE_VALUE.equals(header);
    }
}

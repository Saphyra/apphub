package com.github.saphyra.apphub.api.platform.web_content.server;

import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

public interface LocalizationApiController {
    @RequestMapping(method = RequestMethod.GET, value = Endpoints.TRANSLATE_ERROR_CODE)
    String translate(@RequestParam("error_code") String errorCode, @RequestHeader(Constants.LOCALE_HEADER) String locale);
}

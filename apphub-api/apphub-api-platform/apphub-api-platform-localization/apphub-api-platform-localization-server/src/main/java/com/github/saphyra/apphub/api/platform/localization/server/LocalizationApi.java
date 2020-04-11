package com.github.saphyra.apphub.api.platform.localization.server;

import com.github.saphyra.apphub.lib.endpoint.Endpoint;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

public interface LocalizationApi {
    @RequestMapping(method = RequestMethod.GET, value = Endpoint.TRANSLATE_ERROR_CODE)
    String translate(@RequestParam("error_code") String errorCode, @RequestParam("locale") String locale);
}

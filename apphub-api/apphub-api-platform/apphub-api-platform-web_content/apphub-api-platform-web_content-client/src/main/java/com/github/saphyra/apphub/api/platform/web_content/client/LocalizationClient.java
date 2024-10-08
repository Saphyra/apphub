package com.github.saphyra.apphub.api.platform.web_content.client;

import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.endpoints.GenericEndpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "web-content-localization", url = "${serviceUrls.web-content-localization}")
public interface LocalizationClient {
    @Deprecated
    @RequestMapping(method = RequestMethod.GET, value = GenericEndpoints.TRANSLATE_ERROR_CODE)
    String translate(@RequestParam("error_code") String errorCode, @RequestHeader(Constants.LOCALE_HEADER) String locale);
}

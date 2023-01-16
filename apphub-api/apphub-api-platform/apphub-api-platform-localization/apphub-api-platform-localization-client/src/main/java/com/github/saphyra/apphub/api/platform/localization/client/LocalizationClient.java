package com.github.saphyra.apphub.api.platform.localization.client;

import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.LocalizationKey;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "localization", url = "${serviceUrls.localization}")
public interface LocalizationClient {
    @RequestMapping(method = RequestMethod.GET, value = Endpoints.TRANSLATE_ERROR_CODE)
    String translate(@RequestParam("error_code") String errorCode, @RequestHeader(Constants.LOCALE_HEADER) String locale);

    @GetMapping(Endpoints.TRANSLATE_KEY)
    String translateKey(@RequestParam("key") LocalizationKey key, @RequestHeader(Constants.LOCALE_HEADER) String locale);
}

package com.github.saphyra.apphub.service.platform.web_content;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.error_handler.service.LocalizedMessageProvider;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ErrorController {
    private final LocaleProvider localeProvider;
    private final LocalizedMessageProvider localizedMessageProvider;

    @GetMapping(Endpoints.ERROR_PAGE)
    public ModelAndView errorPage(@RequestParam(name = "error_code", required = false) String errorCode) {
        log.info("Error page called with errorCode {}", errorCode);
        ModelAndView mav = new ModelAndView("err");
        ErrorCode ec = Optional.ofNullable(errorCode)
            .map(ErrorCode::valueOf)
            .orElse(ErrorCode.UNKNOWN_ERROR);
        String localizedMessage = localizedMessageProvider.getLocalizedMessage(localeProvider.getLocaleValidated(), ec);
        mav.addObject("message", localizedMessage);
        mav.addObject("error_code", ec);
        return mav;
    }
}

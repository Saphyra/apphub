package com.github.saphyra.apphub.service.platform.web_content.error_page;

import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.lib.error_handler.service.translation.LocalizedMessageProvider;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Controller
@Slf4j
@RequiredArgsConstructor
class ErrorController {
    private final LocaleProvider localeProvider;
    private final LocalizedMessageProvider localizedMessageProvider;
    private final UserBannedDescriptionResolver userBannedDescriptionResolver;
    private final UserLoggedInQueryService userLoggedInQueryService;

    @GetMapping(Endpoints.ERROR_PAGE)
    ModelAndView errorPage(
        @RequestParam(name = "error_code", required = false) String errorCode,
        @RequestParam(name = "user_id", required = false) UUID userId,
        @RequestParam(name = "required_roles", required = false) String requiredRoles,
        @CookieValue(name = Constants.ACCESS_TOKEN_COOKIE, required = false) UUID accessTokenId
    ) {
        log.info("Error page called with errorCode {}", errorCode);
        ModelAndView mav = new ModelAndView("err");
        ErrorCode ec = Optional.ofNullable(errorCode)
            .map(ErrorCode::valueOf)
            .orElse(ErrorCode.UNKNOWN_ERROR);
        String localizedMessage = localizedMessageProvider.getLocalizedMessage(localeProvider.getLocaleValidated(), ec);
        mav.addObject("message", localizedMessage);
        mav.addObject("error_code", ec);
        mav.addObject("display_logout_button", userLoggedInQueryService.isUserLoggedIn(accessTokenId));

        if (nonNull(userId) && nonNull(requiredRoles)) {
            mav.addObject("description", userBannedDescriptionResolver.resolve(userId, requiredRoles));
        }

        return mav;
    }
}

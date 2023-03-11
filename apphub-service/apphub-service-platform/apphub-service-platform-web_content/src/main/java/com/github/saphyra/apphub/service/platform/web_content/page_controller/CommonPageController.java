package com.github.saphyra.apphub.service.platform.web_content.page_controller;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CommonPageController {
    @GetMapping(Endpoints.CALENDAR_PAGE)
    String calendarPage() {
        return "calendar";
    }

    @GetMapping(Endpoints.MODULES_PAGE)
    public String modules() {
        return "modules";
    }

    @GetMapping(Endpoints.COMMUNITY_PAGE)
    ModelAndView community(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader) {
        ModelAndView mav = new ModelAndView("community/community");
        mav.addObject("userId", accessTokenHeader.getUserId());
        return mav;
    }

    @GetMapping(Endpoints.NOTEBOOK_PAGE)
    String notebook() {
        return "notebook/notebook";
    }
}

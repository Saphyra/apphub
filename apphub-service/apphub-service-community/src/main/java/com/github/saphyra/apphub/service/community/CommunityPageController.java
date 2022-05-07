package com.github.saphyra.apphub.service.community;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
@Slf4j
class CommunityPageController {
    @GetMapping(Endpoints.COMMUNITY_PAGE)
    ModelAndView getIndex(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader) {
        ModelAndView mav = new ModelAndView("community");
        mav.addObject("userId", accessTokenHeader.getUserId());
        return mav;
    }
}

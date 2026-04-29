package com.github.saphyra.apphub.ci.controller;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.value.Environment;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;

import static java.util.Objects.nonNull;

@Controller
@RequestMapping("/settings")
@RequiredArgsConstructor
class SettingsMenuController {
    private final PropertyDao propertyDao;

    @GetMapping
    ModelAndView settingsMenu(
        @RequestParam(name = "success", required = false) String success
    ) {
        ModelAndView modelAndView = new ModelAndView("settings");

        modelAndView.addObject("browser_startup_limit", propertyDao.getBrowserStartupLimit());
        modelAndView.addObject("gui_enabled", propertyDao.isGuiEnabled());
        modelAndView.addObject("environments", Arrays.stream(Environment.values()).map(Environment::name).toList());

        if (nonNull(success)) {
            modelAndView.addObject("success", success);
        }

        return modelAndView;
    }

    @PostMapping
    @Transactional
    String saveSettings(HttpServletRequest request){
        propertyDao.save(PropertyName.BROWSER_STARTUP_LIMIT, Integer.parseInt(request.getParameter("browser_startup_limit")));
        propertyDao.save(PropertyName.GUI_ENABLED, Boolean.parseBoolean(request.getParameter("gui_enabled")));

        return "redirect:/settings?success=saved";
    }
}

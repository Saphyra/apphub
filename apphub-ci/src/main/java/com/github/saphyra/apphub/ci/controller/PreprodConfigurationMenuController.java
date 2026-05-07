package com.github.saphyra.apphub.ci.controller;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import static java.util.Objects.nonNull;

@Controller
@RequiredArgsConstructor
@RequestMapping("/preprod/configure")
@Slf4j
class PreprodConfigurationMenuController {
    private final PropertyDao propertyDao;

    @GetMapping
    ModelAndView configureMenu(@RequestParam(name = "success", required = false) String success) {
        ModelAndView modelAndView = new ModelAndView("preprod_configure");

        modelAndView.addObject("default_build_thread_count", propertyDao.getBuildThreadCountDefault());
        modelAndView.addObject("integration_test_thread_count", propertyDao.getRemoteRunTestsThreadCount());
        modelAndView.addObject("pre_create_webdriver_count", propertyDao.getRemoteRunPreCreateDriverCount());
        modelAndView.addObject("integration_test_retry_count", propertyDao.getRemoteIntegrationRetryCount());
        modelAndView.addObject("service_startup_limit", propertyDao.getRemoteStartupCountLimit());
        modelAndView.addObject("bash_file_location", propertyDao.getBashFileLocation());

        if (nonNull(success)) {
            modelAndView.addObject("success", success);
        }

        log.info("{}", modelAndView);

        return modelAndView;
    }

    @PostMapping
    @Transactional
    String saveConfiguration(HttpServletRequest request) {
        propertyDao.save(PropertyName.BUILD_THREAD_COUNT_DEFAULT, request.getParameter("default_build_thread_count"));
        propertyDao.save(PropertyName.REMOTE_INTEGRATION_TESTS_THREAD_COUNT, request.getParameter("integration_test_thread_count"));
        propertyDao.save(PropertyName.REMOTE_RUN_TESTS_PRE_CREATE_DRIVER_COUNT, request.getParameter("pre_create_webdriver_count"));
        propertyDao.save(PropertyName.REMOTE_INTEGRATION_RETRY_COUNT, request.getParameter("integration_test_retry_count"));
        propertyDao.save(PropertyName.REMOTE_SERVICE_STARTUP_COUNT_LIMIT, request.getParameter("service_startup_limit"));
        propertyDao.save(PropertyName.BASH_FILE_LOCATION, request.getParameter("bash_file_location"));

        return "redirect:/preprod/configure?success=saved";
    }
}

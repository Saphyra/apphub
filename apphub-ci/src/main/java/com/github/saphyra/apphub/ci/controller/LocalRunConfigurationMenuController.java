package com.github.saphyra.apphub.ci.controller;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.value.Service;
import com.github.saphyra.apphub.ci.value.Services;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Controller
@RequestMapping("/local-run/configure")
@RequiredArgsConstructor
@Slf4j
class LocalRunConfigurationMenuController {
    private final PropertyDao propertyDao;
    private final Services services;

    @GetMapping
    ModelAndView configureMenu(@RequestParam(name = "success", required = false) String success) {
        ModelAndView modelAndView = new ModelAndView("local_run_configure");

        modelAndView.addObject("deploy_mode", propertyDao.getLocalDeployMode().name());
        modelAndView.addObject("default_build_thread_count", propertyDao.getBuildThreadCountDefault());
        modelAndView.addObject("skip_test_build_thread_count", propertyDao.getBuildThreadCountSkipTests());
        modelAndView.addObject("disabled_services", propertyDao.getDisabledServices());
        List<String> serviceNames = getOptionalServices()
            .map(Service::getName)
            .sorted(Comparator.naturalOrder())
            .toList();
        modelAndView.addObject("services", serviceNames);
        modelAndView.addObject("integration_test_thread_count", propertyDao.getLocalRunTestsThreadCount());
        modelAndView.addObject("pre_create_webdriver_count", propertyDao.getLocalRunPreCreateDriverCount());
        modelAndView.addObject("integration_test_retry_count", propertyDao.getLocalIntegrationRetryCount());
        modelAndView.addObject("service_startup_limit", propertyDao.getLocalStartupCountLimit());

        if (nonNull(success)) {
            modelAndView.addObject("success", success);
        }

        return modelAndView;
    }

    @PostMapping
    @Transactional
    String saveConfiguration(HttpServletRequest request) {
        propertyDao.save(PropertyName.LOCAL_DEPLOY_MODE, request.getParameter("deploy_mode"));
        propertyDao.save(PropertyName.BUILD_THREAD_COUNT_DEFAULT, request.getParameter("default_build_thread_count"));
        propertyDao.save(PropertyName.BUILD_THREAD_COUNT_SKIP_TESTS, request.getParameter("skip_test_build_thread_count"));
        propertyDao.save(PropertyName.DISABLED_SERVICES, request.getParameter("disabled_services"));
        propertyDao.save(PropertyName.LOCAL_RUN_INTEGRATION_TESTS_THREAD_COUNT, request.getParameter("integration_test_thread_count"));
        propertyDao.save(PropertyName.LOCAL_RUN_TESTS_PRE_CREATE_DRIVER_COUNT, request.getParameter("pre_create_webdriver_count"));
        propertyDao.save(PropertyName.LOCAL_INTEGRATION_RETRY_COUNT, request.getParameter("integration_test_retry_count"));
        propertyDao.save(PropertyName.LOCAL_RUN_SERVICE_STARTUP_COUNT_LIMIT, request.getParameter("service_startup_limit"));
        Enumeration<String> parameterNames = request.getParameterNames();
        List<String> enabledServices = new ArrayList<>();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            if (parameterName.startsWith("disabled_service_")) {
                String serviceName = request.getParameter(parameterName);
                enabledServices.add(serviceName);
            }
        }

        List<String> disabledServices = getOptionalServices()
            .map(Service::getName)
            .filter(name -> !enabledServices.contains(name))
            .toList();

        propertyDao.save(PropertyName.DISABLED_SERVICES, disabledServices);

        return "redirect:/local-run/configure?success=saved";
    }

    private Stream<Service> getOptionalServices() {
        return services.getServices()
            .stream()
            .filter(Service::getOptional);
    }
}

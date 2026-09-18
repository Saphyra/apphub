package com.github.saphyra.apphub.ci.api.controller.v2;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.service.deployment.DeploymentFacade;
import com.github.saphyra.apphub.ci.service.deployment.stop.StopFacade;
import com.github.saphyra.apphub.ci.service.run_test.RunTestFacade;
import com.github.saphyra.apphub.ci.task_queue.TaskQueue;
import com.github.saphyra.apphub.ci.util.RequestParamUtil;
import com.github.saphyra.apphub.ci.value.DefaultProperties;
import com.github.saphyra.apphub.ci.value.Environment;
import com.github.saphyra.apphub.ci.value.Service;
import com.github.saphyra.apphub.ci.value.Services;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Comparator;
import java.util.List;

import static com.github.saphyra.apphub.ci.api.ParameterNames.PARAM_BUILD_THREAD_COUNT;
import static com.github.saphyra.apphub.ci.api.ParameterNames.PARAM_DISABLED_SERVICES;
import static com.github.saphyra.apphub.ci.api.ParameterNames.PARAM_ENVIRONMENT;
import static com.github.saphyra.apphub.ci.api.ParameterNames.PARAM_ERROR;
import static com.github.saphyra.apphub.ci.api.ParameterNames.PARAM_PRE_CREATE_DRIVER_COUNT;
import static com.github.saphyra.apphub.ci.api.ParameterNames.PARAM_RETRY_COUNT;
import static com.github.saphyra.apphub.ci.api.ParameterNames.PARAM_SERVICES;
import static com.github.saphyra.apphub.ci.api.ParameterNames.PARAM_STARTUP_COUNT_LIMIT;
import static com.github.saphyra.apphub.ci.api.ParameterNames.PARAM_SUCCESS;
import static com.github.saphyra.apphub.ci.api.ParameterNames.PARAM_TASK_QUEUE_SIZE;
import static com.github.saphyra.apphub.ci.api.ParameterNames.PARAM_TEST_FILTER;
import static com.github.saphyra.apphub.ci.api.ParameterNames.PARAM_TEST_THREAD_COUNT;
import static com.github.saphyra.apphub.ci.api.ParameterNames.PARAM_USER_DEFINED_SERVICES;
import static java.util.Objects.nonNull;

@Controller
@RequestMapping("/v2/env-ops")
@RequiredArgsConstructor
@Slf4j
class DeploymentController {
    private final TaskQueue taskQueue;
    private final RequestParamUtil requestParamUtil;
    private final DeploymentFacade deploymentFacade;
    private final PropertyDao propertyDao;
    private final Services services;
    private final DefaultProperties defaultProperties;
    private final RunTestFacade runTestFacade;
    private final StopFacade stopFacade;

    @GetMapping("/{environment}")
    ModelAndView deploymentPage(
        @PathVariable(PARAM_ENVIRONMENT) Environment environment,
        @RequestParam(name = PARAM_ERROR, required = false) String error,
        @RequestParam(name = PARAM_SUCCESS, required = false) String success
    ) {
        ModelAndView mav = new ModelAndView("env_ops");

        //Platform
        mav.addObject(PARAM_ENVIRONMENT, environment);
        mav.addObject(PARAM_TASK_QUEUE_SIZE, taskQueue.size());

        if (nonNull(error)) {
            mav.addObject(PARAM_ERROR, error);
        }
        if (nonNull(success)) {
            mav.addObject(PARAM_SUCCESS, success);
        }

        //Deployment
        mav.addObject(PARAM_USER_DEFINED_SERVICES, propertyDao.getStringProperty(PropertyName.USER_DEFINED_SERVICES));
        mav.addObject(PARAM_DISABLED_SERVICES, propertyDao.getDisabledServices());


        List<String> serviceNames = services.getServices()
            .stream()
            .filter(Service::getOptional)
            .map(Service::getName)
            .sorted(Comparator.naturalOrder())
            .toList();
        mav.addObject(PARAM_SERVICES, serviceNames);

        int buildThreadCount = propertyDao.getProperty(PropertyName.BUILD_THREAD_COUNT)
            .map(Integer::parseInt)
            .orElse(defaultProperties.getBuildThreadCountDefault());
        mav.addObject(PARAM_BUILD_THREAD_COUNT, buildThreadCount);

        int startupCountLimit = propertyDao.getProperty(PropertyName.STARTUP_COUNT_LIMIT)
            .map(Integer::parseInt)
            .orElse(defaultProperties.getLocalServiceStartupCountLimit());
        mav.addObject(PARAM_STARTUP_COUNT_LIMIT, startupCountLimit);

        //Testing
        mav.addObject(PARAM_TEST_FILTER, propertyDao.getStringProperty(PropertyName.TEST_FILTER));
        mav.addObject(PARAM_RETRY_COUNT, propertyDao.getProperty(PropertyName.TEST_RETRY_COUNT).map(Integer::parseInt).orElse(defaultProperties.getIntegrationRetryCount()));
        String testThreadCount = propertyDao.getEnvironmentSpecificProperties(PropertyName.TEST_THREAD_COUNT)
            .getForEnvironmentOrDefault(environment, PropertyName.TEST_THREAD_COUNT.name(), String.valueOf(defaultProperties.getLocalRunTestsThreadCount()));
        mav.addObject(PARAM_TEST_THREAD_COUNT, Integer.parseInt(testThreadCount));
        String preCreateDriverCount = propertyDao.getEnvironmentSpecificProperties(PropertyName.PRE_CREATE_DRIVER_COUNT)
            .getForEnvironmentOrDefault(environment, PropertyName.PRE_CREATE_DRIVER_COUNT.name(), String.valueOf(defaultProperties.getLocalRunTestsPreCreateDriverCount()));
        mav.addObject(PARAM_PRE_CREATE_DRIVER_COUNT, Integer.parseInt(preCreateDriverCount));


        log.info("{}", mav);

        return mav;
    }

    @PostMapping("/{environment}/build-and-deploy")
    String buildAndDeploy(@PathVariable(PARAM_ENVIRONMENT) Environment environment, HttpServletRequest request) {
        try {
            List<String> enabledServiceNames = requestParamUtil.extractEnabledServices(request);
            boolean startUserDefinedServices = requestParamUtil.extractStartUserDefinedServices(request);
            List<String> userDefinedServiceNames = requestParamUtil.extractUserDefinedServices(request);
            int buildThreadCount = requestParamUtil.extractBuildThreadCount(request);
            int startupCountLimit = requestParamUtil.extractStartupCountLimit(request);
            boolean skipTests = requestParamUtil.extractSkipTests(request);

            taskQueue.add(() -> deploymentFacade.buildAndDeploy(
                environment,
                enabledServiceNames,
                startUserDefinedServices,
                userDefinedServiceNames,
                buildThreadCount,
                startupCountLimit,
                skipTests
            ));

            return "redirect:/v2/env-ops/" + environment + "?success=Build+and+deploy+task+added+to+queue";
        } catch (IllegalArgumentException e) {
            return "redirect:/v2/env-ops/" + environment + "?error=" + e.getMessage();
        }
    }

    @PostMapping("/{environment}/run-tests")
    String runTests(@PathVariable(PARAM_ENVIRONMENT) Environment environment, HttpServletRequest request) {
        try {
            log.info("Parameters arrived: {}", request.getParameterMap().keySet());
            boolean filterTests = requestParamUtil.extractFilterTests(request);
            String testFilter = filterTests ? requestParamUtil.extractTestFilter(request) : "";
            log.info("Filter tests: {}, test filter: {}", filterTests, testFilter);
            int threadCount = requestParamUtil.extractTestThreadCount(environment, request);
            int preCreatedDriverCount = requestParamUtil.extractPreCreatedDriverCount(environment, request);
            int retryCount = requestParamUtil.extractRetryCount(request);

            taskQueue.add(() -> runTestFacade.runTests(
                environment,
                testFilter,
                threadCount,
                preCreatedDriverCount,
                retryCount
            ));

            return "redirect:/v2/env-ops/" + environment + "?success=Run+tests+task+added+to+queue";
        } catch (IllegalArgumentException e) {
            return "redirect:/v2/env-ops/" + environment + "?error=" + e.getMessage();
        }
    }

    @PostMapping("/{environment}/stop")
    String stop(@PathVariable(PARAM_ENVIRONMENT) Environment environment, HttpServletRequest request) {
        try {
            boolean stopUserDefinedServices = requestParamUtil.extractStopUserDefinedServices(request);
            List<String> userDefinedServiceNames = requestParamUtil.extractUserDefinedServices(request);

            taskQueue.add(() -> stopFacade.stop(
                environment,
                stopUserDefinedServices,
                userDefinedServiceNames
            ));

            return "redirect:/v2/env-ops/" + environment + "?success=Build+and+deploy+task+added+to+queue";
        } catch (IllegalArgumentException e) {
            return "redirect:/v2/env-ops/" + environment + "?error=" + e.getMessage();
        }
    }
}

package com.github.saphyra.apphub.ci.api.controller.v2;

import com.github.saphyra.apphub.ci.api.RequestParamUtil;
import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.service.env_ops.EnvOpsFacade;
import com.github.saphyra.apphub.ci.task_queue.TaskQueue;
import com.github.saphyra.apphub.ci.value.Action;
import com.github.saphyra.apphub.ci.value.DefaultProperties;
import com.github.saphyra.apphub.ci.value.Environment;
import com.github.saphyra.apphub.ci.value.PlatformProperties;
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

import static com.github.saphyra.apphub.ci.api.ApiConstants.PARAM_BUILD_THREAD_COUNT;
import static com.github.saphyra.apphub.ci.api.ApiConstants.PARAM_DISABLED_SERVICES;
import static com.github.saphyra.apphub.ci.api.ApiConstants.PARAM_DYNAMODB_PORT;
import static com.github.saphyra.apphub.ci.api.ApiConstants.PARAM_ENVIRONMENT;
import static com.github.saphyra.apphub.ci.api.ApiConstants.PARAM_ERROR;
import static com.github.saphyra.apphub.ci.api.ApiConstants.PARAM_PRE_CREATE_DRIVER_COUNT;
import static com.github.saphyra.apphub.ci.api.ApiConstants.PARAM_PSQL_PORT;
import static com.github.saphyra.apphub.ci.api.ApiConstants.PARAM_RETRY_COUNT;
import static com.github.saphyra.apphub.ci.api.ApiConstants.PARAM_SERVER_PORT;
import static com.github.saphyra.apphub.ci.api.ApiConstants.PARAM_SERVICES;
import static com.github.saphyra.apphub.ci.api.ApiConstants.PARAM_STARTUP_COUNT_LIMIT;
import static com.github.saphyra.apphub.ci.api.ApiConstants.PARAM_SUCCESS;
import static com.github.saphyra.apphub.ci.api.ApiConstants.PARAM_TASK_QUEUE_SIZE;
import static com.github.saphyra.apphub.ci.api.ApiConstants.PARAM_TEST_FILTER;
import static com.github.saphyra.apphub.ci.api.ApiConstants.PARAM_TEST_THREAD_COUNT;
import static com.github.saphyra.apphub.ci.api.ApiConstants.PARAM_USER_DEFINED_SERVICES;
import static com.github.saphyra.apphub.ci.api.ApiConstants.PATH_ENV_OPS;
import static com.github.saphyra.apphub.ci.api.ApiConstants.PATH_VARIABLE_ENVIRONMENT;
import static com.github.saphyra.apphub.ci.api.ApiConstants.REDIRECT;
import static java.util.Objects.nonNull;

@Controller
@RequestMapping(PATH_ENV_OPS)
@RequiredArgsConstructor
@Slf4j
class EnvOpsController {
    private final TaskQueue taskQueue;
    private final RequestParamUtil requestParamUtil;
    private final EnvOpsFacade envOpsFacade;
    private final PropertyDao propertyDao;
    private final Services services;
    private final DefaultProperties defaultProperties;
    private final PlatformProperties platformProperties;

    @GetMapping
    ModelAndView deploymentPage(
        @PathVariable(PARAM_ENVIRONMENT) Environment environment,
        @RequestParam(name = PARAM_ERROR, required = false) String error,
        @RequestParam(name = PARAM_SUCCESS, required = false) String success
    ) {
        ModelAndView mav = new ModelAndView("env_ops");

        //Platform
        mav.addObject(PARAM_ENVIRONMENT, environment.name());
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
            .orElse(defaultProperties.getBuildThreadCount());
        mav.addObject(PARAM_BUILD_THREAD_COUNT, buildThreadCount);

        int startupCountLimit = propertyDao.getProperty(PropertyName.STARTUP_COUNT_LIMIT)
            .map(Integer::parseInt)
            .orElse(defaultProperties.getServiceStartupCountLimit());
        mav.addObject(PARAM_STARTUP_COUNT_LIMIT, startupCountLimit);

        //Operations
        if (environment == Environment.MINIKUBE) {
            mav.addObject(PARAM_SERVER_PORT, platformProperties.getMinikubeDevServerPort());
            mav.addObject(PARAM_PSQL_PORT, platformProperties.getMinikubeDatabasePort());
            mav.addObject(PARAM_DYNAMODB_PORT, platformProperties.getMinikubeDynamoDbPort());
        } else if (environment == Environment.PREPROD) {
            mav.addObject(PARAM_SERVER_PORT, platformProperties.getMinikubePreprodServerPort());
        }

        //Testing
        mav.addObject(PARAM_TEST_FILTER, propertyDao.getStringProperty(PropertyName.TEST_FILTER));
        mav.addObject(PARAM_RETRY_COUNT, propertyDao.getProperty(PropertyName.TEST_RETRY_COUNT).map(Integer::parseInt).orElse(defaultProperties.getTestRetryCount()));
        String testThreadCount = propertyDao.getEnvironmentSpecificProperties(PropertyName.TEST_THREAD_COUNT)
            .getForEnvironmentOrDefault(environment, PropertyName.TEST_THREAD_COUNT.name(), String.valueOf(defaultProperties.getTestsThreadCount()));
        mav.addObject(PARAM_TEST_THREAD_COUNT, Integer.parseInt(testThreadCount));
        String preCreateDriverCount = propertyDao.getEnvironmentSpecificProperties(PropertyName.PRE_CREATE_DRIVER_COUNT)
            .getForEnvironmentOrDefault(environment, PropertyName.PRE_CREATE_DRIVER_COUNT.name(), String.valueOf(defaultProperties.getPreCreateDriverCount()));
        mav.addObject(PARAM_PRE_CREATE_DRIVER_COUNT, Integer.parseInt(preCreateDriverCount));


        log.debug("{}", mav);

        return mav;
    }

    @PostMapping("/build-and-deploy")
    String buildAndDeploy(@PathVariable(PARAM_ENVIRONMENT) Environment environment, HttpServletRequest request) {
        try {
            Action action = requestParamUtil.extractAction(request);
            List<String> enabledServiceNames = requestParamUtil.extractEnabledServices(request);
            boolean startUserDefinedServices = requestParamUtil.extractStartUserDefinedServices(request);
            List<String> userDefinedServiceNames = requestParamUtil.extractUserDefinedServices(request);
            int buildThreadCount = requestParamUtil.extractBuildThreadCount(request);
            int startupCountLimit = requestParamUtil.extractStartupCountLimit(request);
            boolean skipTests = requestParamUtil.extractSkipTests(request);

            taskQueue.add(() -> envOpsFacade.buildAndDeploy(
                action,
                environment,
                enabledServiceNames,
                startUserDefinedServices,
                userDefinedServiceNames,
                buildThreadCount,
                startupCountLimit,
                skipTests
            ));

            return REDIRECT + PATH_ENV_OPS.replace(PATH_VARIABLE_ENVIRONMENT, environment.name()) + "?success=Build+and+deploy+task+added+to+queue";
        } catch (IllegalArgumentException e) {
            return REDIRECT + PATH_ENV_OPS.replace(PATH_VARIABLE_ENVIRONMENT, environment.name()) + "?error=" + e.getMessage();
        }
    }

    @PostMapping("/run-tests")
    String runTests(@PathVariable(PARAM_ENVIRONMENT) Environment environment, HttpServletRequest request) {
        try {
            log.info("Parameters arrived: {}", request.getParameterMap().keySet());
            boolean filterTests = requestParamUtil.extractFilterTests(request);
            String testFilter = filterTests ? requestParamUtil.extractTestFilter(request) : "";
            log.info("Filter tests: {}, test filter: {}", filterTests, testFilter);
            int threadCount = requestParamUtil.extractTestThreadCount(environment, request);
            int preCreatedDriverCount = requestParamUtil.extractPreCreatedDriverCount(environment, request);
            int retryCount = requestParamUtil.extractRetryCount(request);

            taskQueue.add(() -> envOpsFacade.runTests(
                environment,
                testFilter,
                threadCount,
                preCreatedDriverCount,
                retryCount
            ));

            return REDIRECT + PATH_ENV_OPS.replace(PATH_VARIABLE_ENVIRONMENT, environment.name()) + "?success=Run+tests+task+added+to+queue";
        } catch (IllegalArgumentException e) {
            return REDIRECT + PATH_ENV_OPS.replace(PATH_VARIABLE_ENVIRONMENT, environment.name()) + "?error=" + e.getMessage();
        }
    }

    @PostMapping("/stop")
    String stop(@PathVariable(PARAM_ENVIRONMENT) Environment environment, HttpServletRequest request) {
        try {
            boolean stopUserDefinedServices = requestParamUtil.extractStopUserDefinedServices(request);
            List<String> userDefinedServiceNames = requestParamUtil.extractUserDefinedServices(request);

            taskQueue.add(() -> envOpsFacade.stop(
                environment,
                stopUserDefinedServices,
                userDefinedServiceNames
            ));

            return REDIRECT + PATH_ENV_OPS.replace(PATH_VARIABLE_ENVIRONMENT, environment.name()) + "?success=Stop+task+added+to+queue";
        } catch (IllegalArgumentException e) {
            return REDIRECT + PATH_ENV_OPS.replace(PATH_VARIABLE_ENVIRONMENT, environment.name()) + "?error=" + e.getMessage();
        }
    }
}

package com.github.saphyra.apphub.ci.api;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.value.Environment;
import com.github.saphyra.apphub.ci.value.EnvironmentSpecificProperties;
import com.github.saphyra.apphub.ci.value.Service;
import com.github.saphyra.apphub.ci.value.Services;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import static com.github.saphyra.apphub.ci.api.ApiConstants.PARAM_BUILD_THREAD_COUNT;
import static com.github.saphyra.apphub.ci.api.ApiConstants.PARAM_FILTER_TESTS;
import static com.github.saphyra.apphub.ci.api.ApiConstants.PARAM_PREFIX_ENABLED_SERVICE;
import static com.github.saphyra.apphub.ci.api.ApiConstants.PARAM_PRE_CREATE_DRIVER_COUNT;
import static com.github.saphyra.apphub.ci.api.ApiConstants.PARAM_RETRY_COUNT;
import static com.github.saphyra.apphub.ci.api.ApiConstants.PARAM_SKIP_TESTS;
import static com.github.saphyra.apphub.ci.api.ApiConstants.PARAM_STARTUP_COUNT_LIMIT;
import static com.github.saphyra.apphub.ci.api.ApiConstants.PARAM_START_USER_DEFINED_SERVICES;
import static com.github.saphyra.apphub.ci.api.ApiConstants.PARAM_STOP_USER_DEFINED_SERVICES;
import static com.github.saphyra.apphub.ci.api.ApiConstants.PARAM_TEST_FILTER;
import static com.github.saphyra.apphub.ci.api.ApiConstants.PARAM_TEST_THREAD_COUNT;
import static com.github.saphyra.apphub.ci.api.ApiConstants.PARAM_USER_DEFINED_SERVICES;
import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class RequestParamUtil {
    private final Services services;
    private final PropertyDao propertyDao;

    public List<String> extractEnabledServices(HttpServletRequest request) {
        Enumeration<String> parameterNames = request.getParameterNames();
        List<String> enabledServices = new ArrayList<>();
        // Iterate through all request parameters to find those that start with "disabled_service_"
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            // If the parameter name starts with "disabled_service_", extract the service name and add it to the list of enabled services
            if (parameterName.startsWith(PARAM_PREFIX_ENABLED_SERVICE)) {
                String serviceName = request.getParameter(parameterName);
                enabledServices.add(serviceName);
            }
        }

        // Get the list of all optional services and filter out those that are not in the list of enabled services
        List<String> disabledServices = services.getServices()
            .stream()
            .filter(Service::getOptional)
            .map(Service::getName)
            .filter(name -> !enabledServices.contains(name))
            .toList();

        propertyDao.save(PropertyName.DISABLED_SERVICES, disabledServices);

        return enabledServices;
    }

    public boolean extractStartUserDefinedServices(HttpServletRequest request) {
        return Boolean.parseBoolean(request.getParameter(PARAM_START_USER_DEFINED_SERVICES));
    }

    public List<String> extractUserDefinedServices(HttpServletRequest request) {
        String userDefinedServices = request.getParameter(PARAM_USER_DEFINED_SERVICES);
        if (isNull(userDefinedServices)) {
            throw new IllegalArgumentException("Missing parameter: " + PARAM_USER_DEFINED_SERVICES);
        }

        propertyDao.save(PropertyName.USER_DEFINED_SERVICES, userDefinedServices);

        return List.of(userDefinedServices.split(","));
    }

    public int extractBuildThreadCount(HttpServletRequest request) {
        String buildThreadCountParam = request.getParameter(PARAM_BUILD_THREAD_COUNT);
        if (isNull(buildThreadCountParam)) {
            throw new IllegalArgumentException("Missing parameter: " + PARAM_BUILD_THREAD_COUNT);
        }
        int buildThreadCount = Integer.parseInt(buildThreadCountParam);

        propertyDao.save(PropertyName.BUILD_THREAD_COUNT, String.valueOf(buildThreadCount));

        return buildThreadCount;
    }

    public int extractStartupCountLimit(HttpServletRequest request) {
        String startupCountLimitParam = request.getParameter(PARAM_STARTUP_COUNT_LIMIT);
        if (isNull(startupCountLimitParam)) {
            throw new IllegalArgumentException("Missing parameter: " + PARAM_STARTUP_COUNT_LIMIT);
        }
        int startupCountLimit = Integer.parseInt(startupCountLimitParam);

        propertyDao.save(PropertyName.STARTUP_COUNT_LIMIT, String.valueOf(startupCountLimit));

        return startupCountLimit;
    }

    public boolean extractSkipTests(HttpServletRequest request) {
        return Boolean.parseBoolean(request.getParameter(PARAM_SKIP_TESTS));
    }

    public boolean extractFilterTests(HttpServletRequest request) {
        String parameter = request.getParameter(PARAM_FILTER_TESTS);
        return Boolean.parseBoolean(parameter);
    }

    public String extractTestFilter(HttpServletRequest request) {
        String testFilter = request.getParameter(PARAM_TEST_FILTER);
        if (isNull(testFilter)) {
            throw new IllegalArgumentException("Missing parameter: " + PARAM_TEST_FILTER);
        }

        propertyDao.save(PropertyName.TEST_FILTER, testFilter);

        return testFilter;
    }

    public int extractTestThreadCount(Environment environment, HttpServletRequest request) {
        String testThreadCountParam = request.getParameter(PARAM_TEST_THREAD_COUNT);
        if (isNull(testThreadCountParam)) {
            throw new IllegalArgumentException("Missing parameter: " + PARAM_TEST_THREAD_COUNT);
        }
        int testThreadCount = Integer.parseInt(testThreadCountParam);

        EnvironmentSpecificProperties environmentSpecificProperties = propertyDao.getEnvironmentSpecificProperties(PropertyName.TEST_THREAD_COUNT);
        environmentSpecificProperties.put(environment, Map.of(PropertyName.TEST_THREAD_COUNT.name(), String.valueOf(testThreadCount)));


        propertyDao.save(PropertyName.TEST_THREAD_COUNT, environmentSpecificProperties);

        return testThreadCount;
    }

    public int extractPreCreatedDriverCount(Environment environment, HttpServletRequest request) {
        String testThreadCountParam = request.getParameter(PARAM_PRE_CREATE_DRIVER_COUNT);
        if (isNull(testThreadCountParam)) {
            throw new IllegalArgumentException("Missing parameter: " + PARAM_PRE_CREATE_DRIVER_COUNT);
        }
        int driverCount = Integer.parseInt(testThreadCountParam);

        EnvironmentSpecificProperties environmentSpecificProperties = propertyDao.getEnvironmentSpecificProperties(PropertyName.PRE_CREATE_DRIVER_COUNT);
        environmentSpecificProperties.put(environment, Map.of(PropertyName.PRE_CREATE_DRIVER_COUNT.name(), String.valueOf(driverCount)));


        propertyDao.save(PropertyName.PRE_CREATE_DRIVER_COUNT, environmentSpecificProperties);

        return driverCount;
    }

    public int extractRetryCount(HttpServletRequest request) {
        String retryCountParam = request.getParameter(PARAM_RETRY_COUNT);
        if (isNull(retryCountParam)) {
            throw new IllegalArgumentException("Missing parameter: " + PARAM_RETRY_COUNT);
        }
        int retryCount = Integer.parseInt(retryCountParam);

        propertyDao.save(PropertyName.TEST_RETRY_COUNT, String.valueOf(retryCount));

        return retryCount;
    }

    public boolean extractStopUserDefinedServices(HttpServletRequest request) {
        return Boolean.parseBoolean(request.getParameter(PARAM_STOP_USER_DEFINED_SERVICES));
    }
}

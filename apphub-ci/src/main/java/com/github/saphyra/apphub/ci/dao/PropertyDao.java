package com.github.saphyra.apphub.ci.dao;

import com.github.saphyra.apphub.ci.localization.Language;
import com.github.saphyra.apphub.ci.utils.ObjectMapperWrapper;
import com.github.saphyra.apphub.ci.value.DefaultProperties;
import com.github.saphyra.apphub.ci.value.DeployMode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PropertyDao {
    private final PropertyRepository propertyRepository;
    private final DefaultProperties defaultProperties;
    private final ObjectMapperWrapper objectMapperWrapper;

    public Integer getBuildThreadCountSkipTests() {
        return propertyRepository.findById(PropertyName.BUILD_THREAD_COUNT_SKIP_TESTS)
            .map(Property::getValue)
            .map(Integer::parseInt)
            .orElseGet(defaultProperties::getBuildThreadCountSkipTests);
    }

    public DeployMode getLocalDeployMode() {
        return propertyRepository.findById(PropertyName.REMOTE_DEPLOY_MODE)
            .map(property -> DeployMode.valueOf(property.getValue()))
            .orElseGet(defaultProperties::getLocalDeployMode);
    }

    public DeployMode getRemoteDeployMode() {
        return propertyRepository.findById(PropertyName.REMOTE_DEPLOY_MODE)
            .map(property -> DeployMode.valueOf(property.getValue()))
            .orElseGet(defaultProperties::getRemoteDeployMode);
    }

    public void save(PropertyName propertyName, String value) {
        propertyRepository.save(new Property(propertyName, value));
    }

    public void save(PropertyName propertyName, Object value) {
        propertyRepository.save(new Property(propertyName, objectMapperWrapper.writeValueAsString(value)));
    }

    public Integer getBuildThreadCountDefault() {
        return propertyRepository.findById(PropertyName.BUILD_THREAD_COUNT_DEFAULT)
            .map(Property::getValue)
            .map(Integer::parseInt)
            .orElseGet(defaultProperties::getBuildThreadCountDefault);
    }

    public Integer getThreadCount(DeployMode deployMode) {
        return switch (deployMode) {
            case DEFAULT -> getBuildThreadCountDefault();
            case SKIP_TESTS -> getBuildThreadCountSkipTests();
            default -> throw new IllegalStateException("getThreadCount should not be called with localRunMode " + deployMode);
        };
    }

    public Integer getLocalRunTestsThreadCount() {
        return propertyRepository.findById(PropertyName.LOCAL_RUN_INTEGRATION_TESTS_THREAD_COUNT)
            .map(Property::getValue)
            .map(Integer::parseInt)
            .orElseGet(defaultProperties::getLocalRunTestsThreadCount);
    }

    public Integer getRemoteRunTestsThreadCount() {
        return propertyRepository.findById(PropertyName.REMOTE_INTEGRATION_TESTS_THREAD_COUNT)
            .map(Property::getValue)
            .map(Integer::parseInt)
            .orElseGet(defaultProperties::getRemoteTestsThreadCount);
    }

    public Language getLanguage() {
        return propertyRepository.findById(PropertyName.LANGUAGE)
            .map(property -> Language.valueOf(property.getValue()))
            .orElseGet(() -> Language.valueOf(defaultProperties.getDefaultLocale()));
    }

    public Integer getStartupCountLimit() {
        return propertyRepository.findById(PropertyName.LOCAL_RUN_SERVICE_STARTUP_COUNT_LIMIT)
            .map(Property::getValue)
            .map(Integer::parseInt)
            .orElseGet(defaultProperties::getLocalServiceStartupCountLimit);
    }

    public List<String> getDisabledServices() {
        List<String> disabledServices = propertyRepository.findById(PropertyName.DISABLED_SERVICES)
            .map(Property::getValue)
            .map(value -> objectMapperWrapper.readArrayValue(value, String[].class))
            .orElse(Collections.emptyList());
        return new ArrayList<>(disabledServices);
    }

    public Integer getLocalRunPreCreateDriverCount() {
        return propertyRepository.findById(PropertyName.LOCAL_RUN_TESTS_PRE_CREATE_DRIVER_COUNT)
            .map(Property::getValue)
            .map(Integer::parseInt)
            .orElseGet(defaultProperties::getLocalRunTestsPreCreateDriverCount);
    }

    public Integer getRemoteRunPreCreateDriverCount() {
        return propertyRepository.findById(PropertyName.REMOTE_RUN_TESTS_PRE_CREATE_DRIVER_COUNT)
            .map(Property::getValue)
            .map(Integer::parseInt)
            .orElseGet(defaultProperties::getRemoteRunTestsPreCreateDriverCount);
    }
}

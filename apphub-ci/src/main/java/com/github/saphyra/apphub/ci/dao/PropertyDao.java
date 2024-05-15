package com.github.saphyra.apphub.ci.dao;

import com.github.saphyra.apphub.ci.value.DefaultProperties;
import com.github.saphyra.apphub.ci.value.LocalRunMode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PropertyDao {
    private final PropertyRepository propertyRepository;
    private final DefaultProperties defaultProperties;

    public Integer getLocalRunThreadCountSkipTests() {
        return propertyRepository.findById(PropertyName.LOCAL_RUN_THREAD_COUNT_SKIP_TESTS)
            .map(Property::getValue)
            .map(Integer::parseInt)
            .orElseGet(defaultProperties::getLocalRunThreadCountSkipTests);
    }

    public LocalRunMode getLocalRunMode() {
        return propertyRepository.findById(PropertyName.LOCAL_RUN_MODE)
            .map(property -> LocalRunMode.valueOf(property.getValue()))
            .orElseGet(defaultProperties::getLocalRunMode);
    }

    public void save(PropertyName propertyName, String value) {
        propertyRepository.save(new Property(propertyName, value));
    }

    public Integer getLocalRunThreadCountDefault() {
        return propertyRepository.findById(PropertyName.LOCAL_RUN_THREAD_COUNT_DEFAULT)
            .map(Property::getValue)
            .map(Integer::parseInt)
            .orElseGet(defaultProperties::getLocalRunThreadCountDefault);
    }

    public Integer getThreadCount(LocalRunMode localRunMode) {
        return switch (localRunMode) {
            case DEFAULT -> getLocalRunThreadCountDefault();
            case SKIP_TESTS -> getLocalRunThreadCountSkipTests();
            default -> throw new IllegalStateException("getThreadCount should not be called with localRunMode " + localRunMode);
        };
    }
}

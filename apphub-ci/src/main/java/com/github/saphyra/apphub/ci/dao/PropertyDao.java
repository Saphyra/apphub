package com.github.saphyra.apphub.ci.dao;

import com.github.saphyra.apphub.ci.value.DefaultProperties;
import com.github.saphyra.apphub.ci.value.EnvironmentSpecificProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PropertyDao {
    private final PropertyRepository propertyRepository;
    private final DefaultProperties defaultProperties;
    private final ObjectMapper objectMapper;

    public void save(PropertyName propertyName, String value) {
        propertyRepository.save(new Property(propertyName, value));
    }

    public void save(PropertyName propertyName, Object value) {
        propertyRepository.save(new Property(propertyName, objectMapper.writeValueAsString(value)));
    }

    public List<String> getDisabledServices() {
        List<String> disabledServices = propertyRepository.findById(PropertyName.DISABLED_SERVICES)
            .map(Property::getValue)
            .map(value -> Arrays.asList(objectMapper.readValue(value, String[].class)))
            .orElse(Collections.emptyList());
        return new ArrayList<>(disabledServices);
    }

    public String getBashFileLocation() {
        return propertyRepository.findById(PropertyName.BASH_FILE_LOCATION)
            .map(Property::getValue)
            .orElseGet(defaultProperties::getBashFileLocation);
    }

    public Integer getBrowserStartupLimit() {
        return propertyRepository.findById(PropertyName.BROWSER_STARTUP_LIMIT)
            .map(Property::getValue)
            .map(Integer::parseInt)
            .orElseGet(defaultProperties::getBrowserStartupLimit);
    }

    public boolean isGuiEnabled() {
        return propertyRepository.findById(PropertyName.GUI_ENABLED)
            .map(Property::getValue)
            .map(Boolean::parseBoolean)
            .orElseGet(defaultProperties::isGuiEnabled);
    }

    public EnvironmentSpecificProperties getEnvironmentSpecificProperties(PropertyName propertyName) {
        return propertyRepository.findById(propertyName)
            .map(Property::getValue)
            .map(value -> objectMapper.readValue(value, EnvironmentSpecificProperties.class))
            .orElseGet(EnvironmentSpecificProperties::new);
    }

    public String getStringProperty(PropertyName propertyName) {
        return propertyRepository.findById(propertyName)
            .map(Property::getValue)
            .orElse("");
    }

    public Map<String, String> getDbBackupParams() {
        return propertyRepository.findById(PropertyName.DB_BACKUP)
            .map(Property::getValue)
            .map(value -> objectMapper.readValue(value, Map.class))
            .orElseGet(HashMap::new);
    }

    public Optional<String> getProperty(PropertyName propertyName) {
        return propertyRepository.findById(propertyName)
            .map(Property::getValue);
    }
}

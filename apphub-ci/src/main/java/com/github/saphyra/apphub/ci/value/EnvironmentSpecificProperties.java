package com.github.saphyra.apphub.ci.value;

import java.util.HashMap;
import java.util.Map;

public class EnvironmentSpecificProperties extends HashMap<Environment, Map<String, String>> {
    /**
     * Returns configuration for the specified environment, or the default environment if not set, or empty map if default is not configured
     */
    public Map<String, String> getForEnvironmentOrDefault(Environment environment) {
        return getOrDefault(environment, getOrDefault(Environment.DEFAULT, new HashMap<>()));
    }

    public String getForEnvironmentOrDefault(Environment environment, String key, String defaultValue) {
        return getOrDefault(environment, Map.of(key, defaultValue))
            .get(key);
    }
}

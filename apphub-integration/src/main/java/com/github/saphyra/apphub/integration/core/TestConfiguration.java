package com.github.saphyra.apphub.integration.core;

import com.github.saphyra.apphub.integration.localization.Language;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class TestConfiguration {
    //Platform
    public static final int AVAILABLE_PERMITS = Integer.parseInt(System.getProperty("threadCount", "10"));
    public static final boolean REST_LOGGING_ENABLED = Boolean.parseBoolean(System.getProperty("restLoggingEnabled", "true"));
    public static final boolean WEB_DRIVER_CACHE_ENABLED = Boolean.parseBoolean(System.getProperty("webDriverCacheEnabled", "true"));
    public static final Integer PRE_CREATE_WEB_DRIVERS = Integer.parseInt(System.getProperty("preCreateWebDrivers", "0"));
    public static final boolean WEB_DRIVER_HEADLESS_MODE = Boolean.parseBoolean(System.getProperty("headless", "false"));
    public static final List<String> DISABLED_TEST_GROUPS = Arrays.asList(Optional.ofNullable(System.getProperty("disabledGroups"))
        .orElse("")
        .split(","));
    public static final List<String> ENABLED_TEST_GROUPS = Arrays.asList(Optional.ofNullable(System.getProperty("enabledGroups"))
        .filter(s -> !isBlank(s))
        .orElse("be,fe")
        .split(","));

    //Integration server
    public static final boolean INTEGRATION_SERVER_ENABLED = Boolean.parseBoolean(System.getProperty("integrationServerEnabled", "false"));
    public static final int INTEGRATION_SERVER_PORT = Integer.parseInt(System.getProperty("integrationServerPort", "8072"));

    //Connection
    public static final int SERVER_PORT = Integer.parseInt(Objects.requireNonNull(System.getProperty("serverPort"), "serverPort is null"));
    public static final int DATABASE_PORT = Integer.parseInt(Objects.requireNonNull(System.getProperty("databasePort"), "databasePort is null"));
    public static final String DATABASE_NAME = System.getProperty("databaseName", "postgres");
    public static Connection CONNECTION;

    //Defaults
    public static final Language DEFAULT_LANGUAGE = Language.ENGLISH;
}

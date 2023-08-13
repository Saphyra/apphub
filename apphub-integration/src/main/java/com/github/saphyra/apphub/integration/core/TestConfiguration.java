package com.github.saphyra.apphub.integration.core;

import com.github.saphyra.apphub.integration.framework.DatabaseUtil;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TestConfiguration {
    //Platform
    public static final int AVAILABLE_PERMITS = 10;
    public static final boolean REST_LOGGING_ENABLED = Boolean.parseBoolean(System.getProperty("restLoggingEnabled", "true"));
    public static final List<String> DISABLED_TEST_GROUPS = Arrays.asList(Optional.ofNullable(System.getProperty("disabledGroups"))
        .orElse("")
        .split(","));

    //Connection
    public static final int SERVER_PORT = Integer.parseInt(Objects.requireNonNull(System.getProperty("serverPort"), "serverPort is null"));
    public static final int DATABASE_PORT = Integer.parseInt(Objects.requireNonNull(System.getProperty("databasePort"), "databasePort is null"));
    public static final String DATABASE_NAME = System.getProperty("databaseName", "postgres");
    public static final Connection CONNECTION = DatabaseUtil.getConnection(); //Checking if database is accessible
}

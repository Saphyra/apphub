package com.github.saphyra.apphub.integration.core.util;

import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;

public class CheckedExceptionUtil {
    @SneakyThrows
    public static Connection getConnection(String databaseUrl, String username, String password) {
        return DriverManager.getConnection(databaseUrl, username, password);
    }
}

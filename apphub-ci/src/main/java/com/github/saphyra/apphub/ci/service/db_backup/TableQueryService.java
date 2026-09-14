package com.github.saphyra.apphub.ci.service.db_backup;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
class TableQueryService {
    List<String> getTables(String dbHost, String dbName, String username, String password) {
        List<String> tables = new ArrayList<>();

        String jdbcUrl = DbBackupUtil.getDbUrl(dbHost, dbName);

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            DatabaseMetaData metaData = connection.getMetaData();

            try (ResultSet rs = metaData.getTables(connection.getCatalog(), null, "%", new String[]{"TABLE"}
            )) {
                while (rs.next()) {
                    String schema = rs.getString("TABLE_SCHEM");
                    String table = rs.getString("TABLE_NAME");

                    if (schema == null || schema.isBlank()) {
                        tables.add(table);
                    } else {
                        tables.add(schema + "." + table);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return tables.stream()
            .sorted()
            .toList();
    }
}

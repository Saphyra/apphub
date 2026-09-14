package com.github.saphyra.apphub.ci.service.db_backup;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class TableBatcher {
    @SneakyThrows
    List<List<String>> splitIntoDependencyBatches(String dbUrl, String username, String password, List<String> tables) {
        Map<String, List<String>> dependencies = new HashMap<>();
        tables.forEach(table -> dependencies.put(table, getForeignKeyDependencies(dbUrl, username, password, table)));

        List<List<String>> batches = new ArrayList<>();
        while (!dependencies.isEmpty()) {
            Map<String, Optional<Integer>> batchIndexes = dependencies.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> getBatchIndex(entry.getKey(), entry.getValue(), batches, tables)));
            log.debug("Batches: {}", batches);
            log.debug("batchIndexes: {}", batchIndexes);

            batchIndexes.entrySet()
                .stream()
                .filter(e -> e.getValue().isPresent())
                .forEach(e -> {
                    String tableName = e.getKey();
                    int batchIndex = e.getValue().get();
                    log.debug("Adding table {} to batch {}", tableName, batchIndex);

                    List<String> batch;
                    if (batches.size() > batchIndex) {
                        batch = batches.get(batchIndex);
                    } else {
                        batch = new ArrayList<>();
                        batches.add(batch);
                    }

                    batch.add(tableName);
                    dependencies.remove(tableName);

                    log.debug("Table {} added to batch {}={}", tableName, batchIndex, batch);
                });
        }

        return batches;
    }

    private Optional<Integer> getBatchIndex(String tableName, List<String> dependencies, List<List<String>> batches, List<String> tables) {
        int batchIndex = -0;

        for (String dependency : dependencies) {
            if (tables.contains(dependency)) {
                for (int i = 0; i < batches.size(); i++) {
                    if (batches.get(i).contains(dependency)) {
                        log.debug("{} - Dependent table {} is in batch {}", tableName, dependency, batchIndex);
                        batchIndex = Math.max(batchIndex, i + 1);
                    }
                }

                if (batchIndex == 0) {
                    log.debug("{} - Dependent table {} is not added to a batch yet.", tableName, dependency);
                    return Optional.empty();
                }
            } else {
                log.debug("{} - Dependent table {} is not restored.", tableName, dependency);
            }
        }

        log.debug("{} - Assigned to batch {}", tableName, batchIndex);
        return Optional.of(batchIndex);
    }

    @SneakyThrows
    private List<String> getForeignKeyDependencies(String dbUrl, String username, String password, String table) {
        List<String> deps = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(dbUrl, username, password)) {

            DatabaseMetaData meta = conn.getMetaData();

            String[] parts = table.split("\\.");
            String schema = parts[0];
            String tableName = parts[1];

            try (ResultSet rs = meta.getImportedKeys(conn.getCatalog(), schema, tableName)) {
                while (rs.next()) {
                    int columnCount = rs.getMetaData().getColumnCount();

                    List<String> columns = new ArrayList<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnLabel = rs.getMetaData().getColumnLabel(i);
                        columns.add(columnLabel + "=" + rs.getString(columnLabel));
                    }
                    log.debug(String.join(", ", columns));

                    String fkSchema = rs.getString("PKTABLE_SCHEM");
                    String fkTable = rs.getString("PKTABLE_NAME");

                    String fullName = fkSchema + "." + fkTable;

                    deps.add(fullName);
                }
            }
        }

        log.debug("{} refers to {}", table, deps);
        return deps;
    }
}

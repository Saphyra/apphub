package com.github.saphyra.apphub.ci.service.db_backup;

import com.github.saphyra.apphub.ci.util.concurrent.ExecutorServiceBean;
import com.github.saphyra.apphub.ci.value.BackupLocation;
import com.github.saphyra.apphub.ci.value.BiWrapper;
import com.google.common.base.Stopwatch;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class DbBackupService {
    private final List<BackupService> backupServices;
    private final ExecutorServiceBean executorServiceBean;

    @SneakyThrows
    void backup(
        String dbHost,
        String dbName,
        String username,
        String password,
        String s3AccessKey,
        String s3SecretKey,
        String s3Bucket,
        List<String> tables,
        String version,
        String backupDirectory,
        BackupLocation backupLocation
    ) {
        String dbUrl = DbBackupUtil.getDbUrl(dbHost, dbName);
        String directory = dbName + "/" + version + "/" + LocalDateTime.now().withNano(0);
        log.info("Backing up tables [{}] from database {} to directory {}", tables, dbUrl, directory);

        BackupService backupService = backupServices.stream()
            .filter(bs -> bs.getType() == backupLocation)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unsupported backup location: " + backupLocation));

        Stopwatch stopwatch = Stopwatch.createStarted();

        Map<String, Long> tableSizes = executorServiceBean.processCollectionWithWait(tables, tableName -> new BiWrapper<>(tableName, getTableSize(tableName, dbUrl, username, password)), 8)
            .stream()
            .collect(Collectors.toMap(BiWrapper::getEntity1, BiWrapper::getEntity2));

        tables = tables.stream()
            .sorted((o1, o2) -> Long.compare(tableSizes.getOrDefault(o2, Long.MAX_VALUE), tableSizes.getOrDefault(o1, Long.MAX_VALUE)))
            .toList();

        log.info("Tables sorted by estimated size: {}", tables);

        SnapshotContext snapshot = createSnapshot(dbUrl, username, password);

        backupService.backup(s3AccessKey, s3SecretKey, tables, directory, dbUrl, username, password, s3Bucket, snapshot, backupDirectory);

        stopwatch.stop();
        log.info("Backed up {} tables in {} seconds", tables.size(), stopwatch.elapsed(TimeUnit.SECONDS));
    }

    private long getTableSize(String tableName, String dbUrl, String username, String password) {
        String[] parts = tableName.split("\\.", 2);
        if (parts.length != 2) {
            log.warn("Invalid table name format '{}'. Expected schema.table", tableName);
            return 0;
        }

        String schema = parts[0];
        String table = parts[1];

        String sql = """
            SELECT GREATEST(c.reltuples, 0)::bigint AS estimated_rows
            FROM pg_class c
            JOIN pg_namespace n ON n.oid = c.relnamespace
            WHERE n.nspname = ?
              AND c.relname = ?
              AND c.relkind IN ('r', 'p')
            LIMIT 1
            """;

        try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, schema);
            ps.setString(2, table);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    log.warn("Table not found in pg_class: {}", tableName);
                    return 0;
                }

                long estimate = rs.getLong("estimated_rows");

                log.info("Estimated size for table {}: {} rows", tableName, estimate);

                return estimate;
            }
        } catch (Exception e) {
            log.warn("Failed to read estimated table size for {}. Falling back to Long.MAX_VALUE.", tableName, e);
            return Long.MAX_VALUE;
        }
    }

    private SnapshotContext createSnapshot(String url, String user, String pass) throws Exception {
        Connection conn = DriverManager.getConnection(url, user, pass);
        conn.setAutoCommit(false);

        try (Statement st = conn.createStatement()) {

            st.execute("BEGIN TRANSACTION ISOLATION LEVEL REPEATABLE READ");

            ResultSet rs = st.executeQuery("SELECT pg_export_snapshot()");
            rs.next();
            String snapshotId = rs.getString(1);

            return new SnapshotContext(conn, snapshotId);
        }
    }
}

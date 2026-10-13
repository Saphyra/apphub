package com.github.saphyra.apphub.ci.service.db_backup;

import com.github.saphyra.apphub.ci.value.BackupLocation;
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
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
class DbBackupService {
    private final List<BackupService> backupServices;

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
        SnapshotContext snapshot = createSnapshot(dbUrl, username, password);

        backupService.backup(s3AccessKey, s3SecretKey, tables, directory, dbUrl, username, password, s3Bucket, snapshot, backupDirectory);

        stopwatch.stop();
        log.info("Backed up {} tables in {} seconds", tables.size(), stopwatch.elapsed(TimeUnit.SECONDS));
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

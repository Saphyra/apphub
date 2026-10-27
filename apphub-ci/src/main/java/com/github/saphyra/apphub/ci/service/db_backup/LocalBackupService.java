package com.github.saphyra.apphub.ci.service.db_backup;

import com.github.saphyra.apphub.ci.ui.progress.ProgressIndicator;
import com.github.saphyra.apphub.ci.ui.progress.ProgressIndicatorFactory;
import com.github.saphyra.apphub.ci.util.concurrent.ExecutorServiceBean;
import com.github.saphyra.apphub.ci.util.concurrent.FutureWrapper;
import com.github.saphyra.apphub.ci.value.BackupLocation;
import com.github.saphyra.apphub.ci.value.Constants;
import com.google.common.base.Stopwatch;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

@RequiredArgsConstructor
@Slf4j
@Component
class LocalBackupService implements BackupService {
    private final ExecutorServiceBean readerExecutor = new ExecutorServiceBean(Executors.newFixedThreadPool(8));
    private final ExecutorServiceBean writerExecutor;
    private final ProgressIndicatorFactory progressIndicatorFactory;

    @Override
    public BackupLocation getType() {
        return BackupLocation.LOCAL;
    }

    @Override
    public void backup(String s3AccessKey, String s3SecretKey, List<String> tables, String directory, String dbUrl, String username, String password, String s3Bucket, SnapshotContext snapshot, String backupDirectory) {
        String path = Paths.get(backupDirectory, directory.replace(":", "-")).toString();
        ProgressIndicator progressIndicator = progressIndicatorFactory.createFromLabels(Constants.PANEL_TITLE_LOCAL_BACKUP, tables);

        List<FutureWrapper<Void>> futures = tables.stream()
            .map(table -> readerExecutor.execute(() -> exportTable(progressIndicator, path, dbUrl, username, password, table, snapshot.snapshotId())))
            .toList();

        futures.forEach(fw -> fw.get().getOrThrow());
    }

    @SneakyThrows
    private void exportTable(ProgressIndicator progressIndicator, String directory, String dbUrl, String username, String password, String table, String snapshotId) {
        progressIndicator.started(table);
        Path file = Paths.get(directory, table + ".bin.gz");
        Path parent = file.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        log.info("Exporting table {} to {}", table, file);
        Stopwatch stopwatch = Stopwatch.createStarted();
        try (Connection conn = DriverManager.getConnection(dbUrl, username, password)) {
            conn.setAutoCommit(false);

            try (Statement st = conn.createStatement()) {
                st.execute("BEGIN ISOLATION LEVEL REPEATABLE READ");
                st.execute("SET TRANSACTION SNAPSHOT '" + snapshotId + "'");

                PGConnection pgConn = conn.unwrap(PGConnection.class);
                CopyManager copyManager = pgConn.getCopyAPI();

                String sql = "COPY (SELECT * FROM " + table + ") TO STDOUT WITH BINARY";

                PipedOutputStream pos = new PipedOutputStream();
                PipedInputStream pis = new PipedInputStream(pos, 64 * 1024);

                FutureWrapper<Void> uploadFuture = writerExecutor.execute(() -> writeToFile(file, pis));

                try (GZIPOutputStream gzip = new GZIPOutputStream(pos)) {
                    copyManager.copyOut(sql, gzip);
                }

                uploadFuture.get().getOrThrow();

                conn.commit();

                stopwatch.stop();
                progressIndicator.completed(table);
                log.info("Table {} exported successfully in {} seconds.", table, stopwatch.elapsed(TimeUnit.SECONDS));
            }
        } catch (Exception e) {
            progressIndicator.failed(table);
            throw e;
        }
    }

    @SneakyThrows
    private void writeToFile(Path file, PipedInputStream pis) {
        try (PipedInputStream input = pis; OutputStream output = Files.newOutputStream(file)) {
            input.transferTo(output);
        }
    }
}

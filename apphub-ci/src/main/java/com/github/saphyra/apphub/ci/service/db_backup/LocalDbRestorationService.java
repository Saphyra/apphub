package com.github.saphyra.apphub.ci.service.db_backup;

import com.github.saphyra.apphub.ci.ui.progress.ProgressIndicator;
import com.github.saphyra.apphub.ci.ui.progress.ProgressIndicatorFactory;
import com.github.saphyra.apphub.ci.util.concurrent.ExecutorServiceBean;
import com.github.saphyra.apphub.ci.util.concurrent.FutureWrapper;
import com.github.saphyra.apphub.ci.value.Constants;
import com.google.common.base.Stopwatch;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocalDbRestorationService {
    private final ExecutorServiceBean workerPool = new ExecutorServiceBean(Executors.newFixedThreadPool(4));

    private final TableBatcher tableBatcher;
    private final ObjectMapper objectMapper;
    private final ProgressIndicatorFactory progressIndicatorFactory;

    @SneakyThrows
    void restore(String directory, String dbHost, String dbName, String username, String password) {
        String dbUrl = DbBackupUtil.getDbUrl(dbHost, dbName);

        File[] files = Path.of(directory)
            .toFile()
            .listFiles(File::isFile);

        if (files == null) {
            throw new IllegalArgumentException("Backup directory is not readable: " + directory);
        }

        Map<String, File> tableMap = Arrays.stream(files)
            .collect(Collectors.toMap(this::getTableName, Function.identity()));
        List<String> tables = tableMap.keySet().stream().sorted().toList();

        log.info("Restoring tables {}", tables);
        ProgressIndicator progressIndicator = progressIndicatorFactory.createFromLabels(Constants.PANEL_TITLE_LOCAL_RESTORE, tables);
        Stopwatch stopwatch = Stopwatch.createStarted();
        List<List<String>> tableBatches = tableBatcher.splitIntoDependencyBatches(dbUrl, username, password, tables);
        log.info("Batches: {}", objectMapper.writeValueAsString(tableBatches));

        tableBatches.forEach(batch -> restoreBatch(progressIndicator, dbUrl, username, password, batch, tableMap));

        stopwatch.stop();
        log.info("{} tables restored in {} seconds", tables.size(), stopwatch.elapsed(TimeUnit.SECONDS));
    }

    private void restoreBatch(ProgressIndicator progressIndicator, String dbUrl, String username, String password, List<String> tables, Map<String, File> tableMap) {
        List<FutureWrapper<Void>> futures = tables.stream()
            .map(table -> workerPool.execute(() -> restore(progressIndicator, dbUrl, username, password, table, tableMap.get(table))))
            .toList();

        futures.stream()
            .map(FutureWrapper::get)
            .filter(er -> !er.isSuccess())
            .forEach(er -> log.error("Failed restoring table: {}", er.getException().getMessage(), er.getException()));
    }

    private void restore(ProgressIndicator progressIndicator, String dbUrl, String username, String password, String table, File file) {
        log.info("Restoring {} from {} to {}", table, file, dbUrl);
        progressIndicator.started(table);
        Stopwatch stopwatch = Stopwatch.createStarted();

        try (
            Connection conn = DriverManager.getConnection(dbUrl, username, password);
            InputStream fileStream = Files.newInputStream(file.toPath());
            GZIPInputStream gzip = new GZIPInputStream(fileStream)
        ) {
            conn.setAutoCommit(false);
            conn.createStatement()
                .execute("TRUNCATE TABLE " + table + " CASCADE");

            PGConnection pg = conn.unwrap(PGConnection.class);
            CopyManager copy = pg.getCopyAPI();

            copy.copyIn("COPY " + table + " FROM STDIN WITH (FORMAT BINARY)", gzip);

            conn.commit();
            progressIndicator.completed(table);
        } catch (Exception ex) {
            progressIndicator.failed(table);
            throw new RuntimeException("Failed restoring " + table, ex);
        }

        stopwatch.stop();
        log.info("{} restored in {} seconds.", table, stopwatch.elapsed(TimeUnit.SECONDS));
    }

    private String getTableName(File file) {
        String fileName = file.getName();

        if (fileName.endsWith(".bin.gz")) {
            return fileName.substring(0, fileName.length() - ".bin.gz".length());
        }

        int extensionIndex = fileName.lastIndexOf('.');
        return extensionIndex < 0 ? fileName : fileName.substring(0, extensionIndex);
    }
}

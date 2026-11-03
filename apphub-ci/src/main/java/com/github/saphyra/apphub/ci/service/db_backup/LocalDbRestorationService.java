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
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocalDbRestorationService {
    private final ExecutorServiceBean workerPool = new ExecutorServiceBean(Executors.newFixedThreadPool(4));

    private final TableBatcher tableBatcher;
    private final ObjectMapper objectMapper;
    private final ProgressIndicatorFactory progressIndicatorFactory;
    private final TableRestorer tableRestorer;

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

        tableRestorer.truncate(dbUrl, username, password, tables);

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

        try (InputStream fileStream = Files.newInputStream(file.toPath())) {
            tableRestorer.restore(progressIndicator, dbUrl, username, password, table, fileStream);
        } catch (IOException ex) {
            progressIndicator.failed(table);
            throw new RuntimeException("Failed restoring " + table, ex);
        }
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

package com.github.saphyra.apphub.ci.service.db_backup;

import com.github.saphyra.apphub.ci.ui.progress.ProgressIndicator;
import com.github.saphyra.apphub.ci.ui.progress.ProgressIndicatorFactory;
import com.github.saphyra.apphub.ci.util.concurrent.ExecutionResult;
import com.github.saphyra.apphub.ci.util.concurrent.ExecutorServiceBean;
import com.github.saphyra.apphub.ci.util.concurrent.FutureWrapper;
import com.github.saphyra.apphub.ci.value.Constants;
import com.google.common.base.Stopwatch;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class DbBackupDownloader {
    private final ExecutorServiceBean workerPool = new ExecutorServiceBean(Executors.newFixedThreadPool(16));

    private final ProgressIndicatorFactory progressIndicatorFactory;

    public void downloadBackup(String backupDirectory, String s3AccessKey, String s3SecretKey, String s3Bucket, String database, String version, String backup, List<String> tables) {
        ProgressIndicator progressIndicator = progressIndicatorFactory.createFromLabels(Constants.PANEL_TITLE_DOWNLOAD_BACKUP, tables);
        Stopwatch stopwatch = Stopwatch.createStarted();

        List<FutureWrapper<Void>> futures = tables.stream()
            .map(table -> workerPool.execute(() -> download(progressIndicator, backupDirectory, s3AccessKey, s3SecretKey, s3Bucket, database, version, backup, table)))
            .toList();

        futures.stream()
            .map(FutureWrapper::get)
            .forEach(ExecutionResult::getOrThrow);

        stopwatch.stop();
        log.info("Downloaded {} tables in {} seconds", tables.size(), stopwatch.elapsed(TimeUnit.SECONDS));
    }

    @SneakyThrows
    private void download(ProgressIndicator progressIndicator, String backupDirectory, String s3AccessKey, String s3SecretKey, String s3Bucket, String database, String version, String backup, String table) {
        log.info("Downloading {}/{}/{}/{} to {} from bucket {}", database, version, backup, table, backupDirectory, s3Bucket);

        progressIndicator.started(table);
        Stopwatch stopwatch = Stopwatch.createStarted();
        String s3FileName = database + "/" + version + "/" + backup + "/" + table + ".bin.gz";
        String localFileName = table.replace(":", "-") + ".bin.gz";
        Path localFilePath = Paths.get(backupDirectory, localFileName);
        Path parent = localFilePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (
            S3Client s3Client = DbBackupUtil.createS3Client(s3AccessKey, s3SecretKey);
            ResponseInputStream<?> s3Stream = s3Client.getObject(
                GetObjectRequest.builder()
                    .bucket(s3Bucket)
                    .key(s3FileName)
                    .build()
            )
        ) {
            Files.copy(s3Stream, localFilePath);
            stopwatch.stop();
            log.info("Downloaded {} in {} seconds", table, stopwatch.elapsed().toSeconds());
            progressIndicator.completed(table);
        } catch (IOException e) {
            progressIndicator.failed(table);
            throw new RuntimeException("Failed to download backup for table " + table, e);
        }
    }
}

package com.github.saphyra.apphub.ci.service.db_backup;

import com.github.saphyra.apphub.ci.utils.concurrent.ExecutorServiceBean;
import com.github.saphyra.apphub.ci.utils.concurrent.FutureWrapper;
import com.google.common.base.Stopwatch;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import tools.jackson.databind.ObjectMapper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

@Component
@RequiredArgsConstructor
@Slf4j
class DbRestorationService {
    private final ExecutorServiceBean workerPool = new ExecutorServiceBean(Executors.newFixedThreadPool(4));

    private final TableBatcher tableBatcher;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    void restore(String dbHost, String dbName, String username, String password, String s3AccessKey, String s3SecretKey, String s3Bucket, String database, String version, String backup, List<String> tables) {
        String dbUrl = DbBackupUtil.getDbUrl(dbHost, dbName);
        log.info("Restoring tables {}", tables);
        Stopwatch stopwatch = Stopwatch.createStarted();
        List<List<String>> tableBatches = tableBatcher.splitIntoDependencyBatches(dbUrl, username, password, tables);
        log.info("Batches: {}", objectMapper.writeValueAsString(tableBatches));

            tableBatches.forEach(batch -> restoreBatch(dbUrl, username, password, s3AccessKey, s3SecretKey, s3Bucket, database, version, backup, batch));

        stopwatch.stop();
        log.info("{} tables restored in {} seconds", tables.size(), stopwatch.elapsed(TimeUnit.SECONDS));
    }

    private void restoreBatch(String dbUrl, String username, String password, String s3AccessKey, String s3SecretKey, String s3Bucket, String database, String version, String backup, List<String> tables) {
        List<FutureWrapper<Void>> futures = tables.stream()
            .map(table -> workerPool.execute(() -> restore(dbUrl, username, password, s3AccessKey, s3SecretKey, s3Bucket, database, version, backup, table)))
            .toList();

        futures.forEach(fw -> fw.get().getOrThrow());
    }

    private void restore(String dbUrl, String username, String password, String s3AccessKey, String s3SecretKey, String s3Bucket, String database, String version, String backup, String table) {
        log.info("Restoring {}/{}/{}/{} to {} from bucket {}", database, version,  backup, table, dbUrl, s3Bucket);
        Stopwatch stopwatch = Stopwatch.createStarted();
        String key = database + "/" + version + "/" + backup + "/" + table + ".bin.gz";

        try (
            Connection conn = DriverManager.getConnection(dbUrl, username, password);
            S3Client s3Client = DbBackupUtil.createS3Client(s3AccessKey, s3SecretKey);
            ResponseInputStream<?> s3Stream = s3Client.getObject(
                GetObjectRequest.builder()
                    .bucket(s3Bucket)
                    .key(key)
                    .build());
            GZIPInputStream gzip = new GZIPInputStream(s3Stream)
        ) {
            conn.setAutoCommit(false);
            conn.createStatement()
                .execute("TRUNCATE TABLE " + table + " CASCADE");

            PGConnection pg = conn.unwrap(PGConnection.class);
            CopyManager copy = pg.getCopyAPI();

            copy.copyIn("COPY " + table + " FROM STDIN WITH (FORMAT BINARY)", gzip);

            conn.commit();
        } catch (Exception ex) {
            throw new RuntimeException("Failed restoring " + table, ex);
        }

        stopwatch.stop();
        log.info("{} restored in {} seconds.", table, stopwatch.elapsed(TimeUnit.SECONDS));
    }
}

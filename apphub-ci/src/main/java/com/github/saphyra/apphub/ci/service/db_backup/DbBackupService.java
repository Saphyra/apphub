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
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

import static com.github.saphyra.apphub.ci.service.db_backup.DbBackupUtil.createS3Client;

@Component
@RequiredArgsConstructor
@Slf4j
class DbBackupService {
    private final ExecutorServiceBean readerExecutor = new ExecutorServiceBean(Executors.newFixedThreadPool(8));
    private final ExecutorServiceBean writerExecutor;

    @SneakyThrows
    void backup(String dbHost, String dbName, String username, String password, String s3AccessKey, String s3SecretKey, String s3Bucket, List<String> tables, String version) {
        String dbUrl = DbBackupUtil.getDbUrl(dbHost, dbName);
        String directory = dbName + "/" + version + "/" + LocalDateTime.now().withNano(0);
        log.info("Backing up tables [{}] from database {} to bucket {} directory {}", tables, dbUrl, s3Bucket, directory);
        Stopwatch stopwatch = Stopwatch.createStarted();
        try (S3Client s3Client = createS3Client(s3AccessKey, s3SecretKey)) {
            SnapshotContext snapshot = createSnapshot(dbUrl, username, password);

            List<FutureWrapper<Void>> futures = tables.stream()
                .map(table -> readerExecutor.execute(() -> exportTable(directory, dbUrl, username, password, s3Bucket, s3Client, table, snapshot.snapshotId())))
                .toList();

            futures.forEach(fw -> fw.get().getOrThrow());
        }

        stopwatch.stop();
        log.info("Backed up {} tables in {} seconds", tables.size(), stopwatch.elapsed(TimeUnit.SECONDS));
    }

    @SneakyThrows
    private void exportTable(String directory, String dbUrl, String username, String password, String bucket, S3Client s3Client, String table, String snapshotId) {
        String key = directory + "/" + table + ".bin.gz";
        log.info("Exporting table {} to {}", table, key);
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

                FutureWrapper<Void> uploadFuture = writerExecutor.execute(() -> uploadToS3Multipart(bucket, key, pis, s3Client));

                try (GZIPOutputStream gzip = new GZIPOutputStream(pos)) {
                    copyManager.copyOut(sql, gzip);
                }

                uploadFuture.get().getOrThrow();

                conn.commit();

                stopwatch.stop();
                log.info("Table {} exported successfully in {} seconds.", table, stopwatch.elapsed(TimeUnit.SECONDS));
            }
        }
    }

    @SneakyThrows
    private void uploadToS3Multipart(String bucket, String key, InputStream input, S3Client s3Client) {
        CreateMultipartUploadResponse create = s3Client.createMultipartUpload(
            CreateMultipartUploadRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType("application/gzip")
                .build()
        );

        String uploadId = create.uploadId();

        List<CompletedPart> parts = new ArrayList<>();
        int BUFFER_SIZE = 8 * 1024 * 1024; // 8MB
        int MIN_PART_SIZE = 5 * 1024 * 1024; // 8MB

        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            ByteArrayOutputStream partBuffer = new ByteArrayOutputStream(BUFFER_SIZE);

            int bytesRead;
            int partNumber = 1;

            while ((bytesRead = input.read(buffer)) != -1) {

                partBuffer.write(buffer, 0, bytesRead);

                // ONLY upload when >= 5MB
                if (partBuffer.size() >= MIN_PART_SIZE) {
                    parts.add(uploadPart(s3Client, bucket, key, uploadId, partNumber++, partBuffer.toByteArray()));
                    partBuffer.reset();
                }
            }

            // last part (can be < 5MB)
            if (partBuffer.size() > 0) {
                parts.add(uploadPart(s3Client, bucket, key, uploadId, partNumber, partBuffer.toByteArray()));
            }

            s3Client.completeMultipartUpload(
                CompleteMultipartUploadRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .uploadId(uploadId)
                    .multipartUpload(
                        CompletedMultipartUpload.builder()
                            .parts(parts)
                            .build()
                    )
                    .build()
            );
        } catch (Exception e) {
            s3Client.abortMultipartUpload(
                AbortMultipartUploadRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .uploadId(uploadId)
                    .build()
            );
            throw e;
        }
    }

    private CompletedPart uploadPart(S3Client s3Client, String bucket, String key, String uploadId, int partNumber, byte[] data) {
        UploadPartResponse resp = s3Client.uploadPart(
            UploadPartRequest.builder()
                .bucket(bucket)
                .key(key)
                .uploadId(uploadId)
                .partNumber(partNumber)
                .contentLength((long) data.length)
                .build(),
            RequestBody.fromBytes(data)
        );

        return CompletedPart.builder()
            .partNumber(partNumber)
            .eTag(resp.eTag())
            .build();
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

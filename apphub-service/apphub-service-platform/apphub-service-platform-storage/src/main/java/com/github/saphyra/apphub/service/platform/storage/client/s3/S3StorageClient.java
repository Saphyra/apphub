package com.github.saphyra.apphub.service.platform.storage.client.s3;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.platform.storage.dao.Storage;
import com.github.saphyra.apphub.service.platform.storage.client.DownloadResult;
import com.github.saphyra.apphub.service.platform.storage.client.StorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.StorageClass;

import java.io.InputStream;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@S3ClientEnabled
public class S3StorageClient implements StorageClient {
    private final S3Client s3Client;
    private final S3Properties s3Properties;
    private final UuidConverter uuidConverter;
    private final ErrorReporterService errorReporterService;

    @Override
    public Storage getType() {
        return Storage.S3;
    }

    @Override
    public DownloadResult download(UUID storedFileId) {
        GetObjectRequest request = GetObjectRequest.builder()
            .bucket(s3Properties.getBucketName())
            .key(uuidConverter.convertDomain(storedFileId))
            .build();

        return DownloadResult.builder()
            .inputStream(s3Client.getObject(request))
            .build();
    }

    @Override
    public void upload(UUID storedFileId, InputStream file, long fileSize) {
        PutObjectRequest request = PutObjectRequest.builder()
            .bucket(s3Properties.getBucketName())
            .key(uuidConverter.convertDomain(storedFileId))
            .storageClass(StorageClass.INTELLIGENT_TIERING)
            .build();

        s3Client.putObject(request, RequestBody.fromInputStream(file, fileSize));
    }

    @Override
    public void delete(UUID storedFileId) {
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(s3Properties.getBucketName())
                .key(uuidConverter.convertDomain(storedFileId))
                .build();

            s3Client.deleteObject(request);
        } catch (Exception e) {
            errorReporterService.report("Failed deleting FTP file " + storedFileId, e);
        }
    }
}

package com.github.saphyra.apphub.ci.service.db_backup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class S3ListService {
    List<String> readDir(String s3AccessKey, String s3SecretKey, String bucket, String prefix) {
        log.info("Reading children of '{}'", prefix);
        try (S3Client s3Client = DbBackupUtil.createS3Client(s3AccessKey, s3SecretKey)) {
            ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucket)
                .prefix(prefix)
                .delimiter("/")
                .build();

            ListObjectsV2Response response = s3Client.listObjectsV2(request);
            List<String> result = new ArrayList<>();

            response.contents()
                .forEach(s3Object -> result.add(s3Object.key().substring(prefix.length()).replace("/", "")));
            response.commonPrefixes()
                .forEach(commonPrefix -> result.add(commonPrefix.prefix().substring(prefix.length()).replace("/", "")));

            result.forEach(child -> log.info("Child found: {}", child));

            return result;
        }
    }
}

package com.github.saphyra.apphub.ci.service.db_backup;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class DbBackupUtil {
    public static String getDbUrl(String dbHost, String dbName) {
        return "jdbc:postgresql://%s/%s?ApplicationName=ApphubCI".formatted(dbHost, dbName);
    }

    public static S3Client createS3Client(String s3AccessKey, String s3SecretKey) {
        AwsBasicCredentials credentials = AwsBasicCredentials.builder()
            .accessKeyId(s3AccessKey)
            .secretAccessKey(s3SecretKey)
            .build();
        return S3Client.builder()
            .region(Region.EU_CENTRAL_1)
            .credentialsProvider(() -> credentials)
            .build();
    }
}

package com.github.saphyra.apphub.ci.service.db_backup;

import com.github.saphyra.apphub.ci.value.BackupLocation;

import java.util.List;

interface BackupService {
    BackupLocation getType();

    void backup(String s3AccessKey, String s3SecretKey, List<String> tables, String directory, String dbUrl, String username, String password, String s3Bucket, SnapshotContext snapshot, String backupDirectory);
}

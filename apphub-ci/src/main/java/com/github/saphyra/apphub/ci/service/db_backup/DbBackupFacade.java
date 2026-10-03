package com.github.saphyra.apphub.ci.service.db_backup;

import com.github.saphyra.apphub.ci.util.concurrent.ExecutorServiceBean;
import com.github.saphyra.apphub.ci.value.BackupLocation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DbBackupFacade {
    private final TableQueryService tableQueryService;
    private final DbBackupService dbBackupService;
    private final ExecutorServiceBean executorServiceBean;
    private final S3ListService s3ListService;
    private final S3DbRestorationService s3DbRestorationService;
    private final LocalDbRestorationService localDbRestorationService;

    /**
     * @return all the tables available in the given database
     */
    public List<String> getTables(String dbHost, String dbName, String username, String password) {
        return tableQueryService.getTables(dbHost, dbName, username, password);
    }

    public void backup(
        String dbHost,
        String dbName, String username,
        String password,
        String s3AccessKey,
        String s3SecretKey,
        String s3Bucket,
        List<String> tables,
        String version,
        String backupDirectory,
        List<BackupLocation> backupLocations
    ) {
        backupLocations.forEach(backupLocation -> executorServiceBean.execute(() -> dbBackupService.backup(dbHost, dbName, username, password, s3AccessKey, s3SecretKey, s3Bucket, tables, version, backupDirectory, backupLocation)));
    }

    public void restore(String dbHost, String dbName, String username, String password, String s3AccessKey, String s3SecretKey, String s3Bucket, String database, String version, String backup, List<String> tables) {
        executorServiceBean.execute(() -> s3DbRestorationService.restore(dbHost, dbName, username, password, s3AccessKey, s3SecretKey, s3Bucket, database, version, backup, tables));
    }

    public List<String> getDatabases(String s3AccessKey, String s3SecretKey, String bucket) {
        return s3ListService.readDir(s3AccessKey, s3SecretKey, bucket, "");
    }

    public List<String> getVersions(String s3AccessKey, String s3SecretKey, String bucket, String database) {
        return s3ListService.readDir(s3AccessKey, s3SecretKey, bucket, database + "/");
    }

    public List<String> getBackups(String s3AccessKey, String s3SecretKey, String bucket, String database, String version) {
        return s3ListService.readDir(s3AccessKey, s3SecretKey, bucket, database + "/" + version + "/");
    }

    /**
     * @return list of tables in the given backup of given database
     */
    public List<String> getTables(String s3AccessKey, String s3SecretKey, String bucket, String database, String version, String backup) {
        return s3ListService.readDir(s3AccessKey, s3SecretKey, bucket, database + "/" + version + "/" + backup + "/")
            .stream()
            .map(table -> table.replace(".bin.gz", ""))
            .toList();
    }

    public void restoreLocal(String directory, String dbHost, String dbName, String username, String password) {
        localDbRestorationService.restore(directory, dbHost, dbName, username, password);
    }
}

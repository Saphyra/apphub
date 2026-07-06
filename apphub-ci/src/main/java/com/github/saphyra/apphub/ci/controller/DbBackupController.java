package com.github.saphyra.apphub.ci.controller;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.service.db_backup.DbBackupFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;

@RequestMapping(DbBackupController.PAGE_DB_BACKUP_INDEX)
@Controller
@RequiredArgsConstructor
@Slf4j
class DbBackupController {
    public static final String PAGE_DB_BACKUP_INDEX = "/db-backup";
    private static final String PAGE_BACKUP = "/backup";
    private static final String PAGE_RESTORE = "/restore";
    private static final String PAGE_RESTORE_DATABASE = "/restore/{database}";
    private static final String PAGE_RESTORE_DATABASE_BACKUP = "/restore/{database}/{backup}";

    private static final String REQUEST_PARAM_DB_HOST = "dbhost";
    private static final String REQUEST_PARAM_DB_NAME = "dbname";
    private static final String REQUEST_PARAM_USERNAME = "username";
    private static final String REQUEST_PARAM_PASSWORD = "password";
    private static final String REQUEST_PARAM_TABLES = "tables";
    private static final String REQUEST_PARAM_S3_ACCESS_KEY = "s3accesskey";
    private static final String REQUEST_PARAM_S3_SECRET_KEY = "s3secretkey";
    private static final String REQUEST_PARAM_S3_BUCKET = "s3bucket";
    private static final String REQUEST_PARAM_DATABASE = "database";
    private static final String REQUEST_PARAM_DATABASES = "databases";
    private static final String REQUEST_PARAM_BACKUPS = "backups";
    private static final String REQUEST_PARAM_BACKUP = "backup";

    private final DbBackupFacade dbBackupFacade;
    private final PropertyDao propertyDao;

    @GetMapping
    ModelAndView dbBackupPage(@RequestParam(name = "success", required = false) String success) {
        ModelAndView mav = new ModelAndView("db_backup");

        Map<String, String> params = propertyDao.getDbBackupParams();
        mav.addObject(REQUEST_PARAM_DB_HOST, params.get(REQUEST_PARAM_DB_HOST));
        mav.addObject(REQUEST_PARAM_DB_NAME, params.get(REQUEST_PARAM_DB_NAME));
        mav.addObject(REQUEST_PARAM_USERNAME, params.get(REQUEST_PARAM_USERNAME));
        mav.addObject(REQUEST_PARAM_PASSWORD, params.get(REQUEST_PARAM_PASSWORD));
        mav.addObject(REQUEST_PARAM_S3_ACCESS_KEY, params.get(REQUEST_PARAM_S3_ACCESS_KEY));
        mav.addObject(REQUEST_PARAM_S3_SECRET_KEY, params.get(REQUEST_PARAM_S3_SECRET_KEY));
        mav.addObject(REQUEST_PARAM_S3_BUCKET, params.get(REQUEST_PARAM_S3_BUCKET));

        if (nonNull(success)) {
            mav.addObject("success", success);
        }

        return mav;
    }

    @PostMapping
    String dbBackupSettings(
        @RequestParam(REQUEST_PARAM_DB_HOST) String dbHost,
        @RequestParam(REQUEST_PARAM_DB_NAME) String dbName,
        @RequestParam(REQUEST_PARAM_USERNAME) String username,
        @RequestParam(REQUEST_PARAM_PASSWORD) String password,
        @RequestParam(REQUEST_PARAM_S3_ACCESS_KEY) String s3AccessKey,
        @RequestParam(REQUEST_PARAM_S3_SECRET_KEY) String s3SecretKey,
        @RequestParam(REQUEST_PARAM_S3_BUCKET) String s3Bucket
    ) {
        Map<String, String> params = Map.of(
            REQUEST_PARAM_DB_HOST, dbHost,
            REQUEST_PARAM_DB_NAME, dbName,
            REQUEST_PARAM_USERNAME, username,
            REQUEST_PARAM_PASSWORD, password,
            REQUEST_PARAM_S3_ACCESS_KEY, s3AccessKey,
            REQUEST_PARAM_S3_SECRET_KEY, s3SecretKey,
            REQUEST_PARAM_S3_BUCKET, s3Bucket
        );
        Map<String, String> storedParams = propertyDao.getDbBackupParams();
        storedParams.putAll(params);
        propertyDao.save(PropertyName.DB_BACKUP, storedParams);

        return "redirect:" + PAGE_DB_BACKUP_INDEX + "?success=saved";
    }

    @GetMapping(PAGE_BACKUP)
    ModelAndView backupPage() {
        ModelAndView mav = new ModelAndView("db_backup_backup");

        Map<String, String> storedParams = propertyDao.getDbBackupParams();
        String dbHost = storedParams.get(REQUEST_PARAM_DB_HOST);
        String dbName = storedParams.get(REQUEST_PARAM_DB_NAME);
        String username = storedParams.get(REQUEST_PARAM_USERNAME);
        String password = storedParams.get(REQUEST_PARAM_PASSWORD);

        mav.addObject(REQUEST_PARAM_DB_HOST, dbHost);
        mav.addObject(REQUEST_PARAM_DB_NAME, dbName);
        mav.addObject(REQUEST_PARAM_S3_BUCKET, storedParams.get(REQUEST_PARAM_S3_BUCKET));

        List<String> tables = dbBackupFacade.getTables(dbHost, dbName, username, password);
        mav.addObject(REQUEST_PARAM_TABLES, tables);

        return mav;
    }

    @PostMapping(PAGE_BACKUP)
    String backup(@RequestParam(REQUEST_PARAM_TABLES) List<String> tables) {
        Map<String, String> storedParams = propertyDao.getDbBackupParams();
        String dbHost = storedParams.get(REQUEST_PARAM_DB_HOST);
        String dbName = storedParams.get(REQUEST_PARAM_DB_NAME);
        String username = storedParams.get(REQUEST_PARAM_USERNAME);
        String password = storedParams.get(REQUEST_PARAM_PASSWORD);
        String s3AccessKey = storedParams.get(REQUEST_PARAM_S3_ACCESS_KEY);
        String s3SecretKey = storedParams.get(REQUEST_PARAM_S3_SECRET_KEY);
        String s3Bucket = storedParams.get(REQUEST_PARAM_S3_BUCKET);

        dbBackupFacade.backup(dbHost, dbName, username, password, s3AccessKey, s3SecretKey, s3Bucket, tables);

        return "redirect:" + PAGE_DB_BACKUP_INDEX + "?success=backup_started";
    }

    @GetMapping(PAGE_RESTORE)
    ModelAndView restorePage() {
        ModelAndView mav = new ModelAndView("db_backup_restore");

        Map<String, String> storedParams = propertyDao.getDbBackupParams();

        String dbHost = storedParams.get(REQUEST_PARAM_DB_HOST);
        String dbName = storedParams.get(REQUEST_PARAM_DB_NAME);
        String bucket = storedParams.get(REQUEST_PARAM_S3_BUCKET);
        String s3AccessKey = storedParams.get(REQUEST_PARAM_S3_ACCESS_KEY);
        String s3SecretKey = storedParams.get(REQUEST_PARAM_S3_SECRET_KEY);

        mav.addObject(REQUEST_PARAM_DB_HOST, dbHost);
        mav.addObject(REQUEST_PARAM_DB_NAME, dbName);
        mav.addObject(REQUEST_PARAM_S3_BUCKET, bucket);

        List<String> databases = dbBackupFacade.getDatabases(s3AccessKey, s3SecretKey, bucket);
        mav.addObject(REQUEST_PARAM_DATABASES, databases);

        return mav;
    }

    @GetMapping(PAGE_RESTORE_DATABASE)
    ModelAndView restoreDatabasePage(@PathVariable("database") String database) {
        log.info("Reading backups of database {}", database);
        ModelAndView mav = new ModelAndView("db_backup_restore_database");

        Map<String, String> storedParams = propertyDao.getDbBackupParams();

        String dbHost = storedParams.get(REQUEST_PARAM_DB_HOST);
        String dbName = storedParams.get(REQUEST_PARAM_DB_NAME);
        String bucket = storedParams.get(REQUEST_PARAM_S3_BUCKET);
        String s3AccessKey = storedParams.get(REQUEST_PARAM_S3_ACCESS_KEY);
        String s3SecretKey = storedParams.get(REQUEST_PARAM_S3_SECRET_KEY);

        mav.addObject(REQUEST_PARAM_DB_HOST, dbHost);
        mav.addObject(REQUEST_PARAM_DB_NAME, dbName);
        mav.addObject(REQUEST_PARAM_S3_BUCKET, bucket);
        mav.addObject(REQUEST_PARAM_DATABASE, database);

        List<String> backups = dbBackupFacade.getBackups(s3AccessKey, s3SecretKey, bucket, database);
        mav.addObject(REQUEST_PARAM_BACKUPS, backups);

        return mav;
    }

    @GetMapping(PAGE_RESTORE_DATABASE_BACKUP)
    ModelAndView restoreDatabaseBackupPage(
        @PathVariable(REQUEST_PARAM_DATABASE) String database,
        @PathVariable(REQUEST_PARAM_BACKUP) String backup
    ) {
        log.info("Reading tables of database {} and backup {}", database, backup);
        ModelAndView mav = new ModelAndView("db_backup_restore_database_backup");

        Map<String, String> storedParams = propertyDao.getDbBackupParams();

        String dbHost = storedParams.get(REQUEST_PARAM_DB_HOST);
        String dbName = storedParams.get(REQUEST_PARAM_DB_NAME);
        String bucket = storedParams.get(REQUEST_PARAM_S3_BUCKET);
        String s3AccessKey = storedParams.get(REQUEST_PARAM_S3_ACCESS_KEY);
        String s3SecretKey = storedParams.get(REQUEST_PARAM_S3_SECRET_KEY);

        mav.addObject(REQUEST_PARAM_DB_HOST, dbHost);
        mav.addObject(REQUEST_PARAM_DB_NAME, dbName);
        mav.addObject(REQUEST_PARAM_S3_BUCKET, bucket);
        mav.addObject(REQUEST_PARAM_DATABASE, database);
        mav.addObject(REQUEST_PARAM_BACKUP, backup);

        List<String> tables = dbBackupFacade.getTables(s3AccessKey, s3SecretKey, bucket, database, backup);
        mav.addObject(REQUEST_PARAM_TABLES, tables);

        return mav;
    }

    @PostMapping(PAGE_RESTORE_DATABASE_BACKUP)
    String restoreDatabase(
        @PathVariable(REQUEST_PARAM_DATABASE) String database,
        @PathVariable(REQUEST_PARAM_BACKUP) String backup,
        @RequestParam(REQUEST_PARAM_TABLES) List<String> tables
    ){
        Map<String, String> storedParams = propertyDao.getDbBackupParams();
        String dbHost = storedParams.get(REQUEST_PARAM_DB_HOST);
        String dbName = storedParams.get(REQUEST_PARAM_DB_NAME);
        String username = storedParams.get(REQUEST_PARAM_USERNAME);
        String password = storedParams.get(REQUEST_PARAM_PASSWORD);
        String s3AccessKey = storedParams.get(REQUEST_PARAM_S3_ACCESS_KEY);
        String s3SecretKey = storedParams.get(REQUEST_PARAM_S3_SECRET_KEY);
        String s3Bucket = storedParams.get(REQUEST_PARAM_S3_BUCKET);

        dbBackupFacade.restore(dbHost, dbName, username, password, s3AccessKey, s3SecretKey, s3Bucket, database, backup, tables);

        return "redirect:" + PAGE_DB_BACKUP_INDEX + "?success=restoration_started";
    }
}

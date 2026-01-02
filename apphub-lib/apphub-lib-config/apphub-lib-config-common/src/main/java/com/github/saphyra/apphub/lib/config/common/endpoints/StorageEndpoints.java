package com.github.saphyra.apphub.lib.config.common.endpoints;

public class StorageEndpoints {
    public static final String EVENT_CLEAN_UP_STORED_FILES = "/event/storage/cleanup/stored-files";
    public static final String EVENT_FILE_CLEANUP = "/api/storage/cleanup/files";

    public static final String STORAGE_UPLOAD_FILE = "/api/storage/{storedFileId}";
    public static final String STORAGE_INTERNAL_DELETE_FILE = "/internal/storage/{storedFileId}";
    public static final String STORAGE_DOWNLOAD_FILE = "/api/storage/{storedFileId}";
    public static final String STORAGE_INTERNAL_CREATE_FILE = "/internal/storage";
    public static final String STORAGE_GET_METADATA = "/api/storage/{storedFileId}/metadata";
}

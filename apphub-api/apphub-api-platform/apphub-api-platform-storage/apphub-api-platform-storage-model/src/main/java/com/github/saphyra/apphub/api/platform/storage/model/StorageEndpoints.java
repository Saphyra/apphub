package com.github.saphyra.apphub.api.platform.storage.model;

public class StorageEndpoints {
    public static final String STORAGE_UPLOAD_FILE = "/api/storage/{storedFileId}";
    public static final String STORAGE_INTERNAL_DELETE_FILE = "/internal/storage/{storedFileId}";
    public static final String STORAGE_DOWNLOAD_FILE = "/api/storage/{storedFileId}";
    public static final String STORAGE_INTERNAL_CREATE_FILE = "/internal/storage";
    public static final String STORAGE_GET_METADATA = "/api/storage/{storedFileId}/metadata";
    public static final String STORAGE_INTERNAL_CLONE_FILE = "/internal/storage/{storedFileId}/clone";
}

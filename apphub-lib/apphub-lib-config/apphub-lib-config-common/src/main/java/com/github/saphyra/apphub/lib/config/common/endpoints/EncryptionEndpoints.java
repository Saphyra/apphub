package com.github.saphyra.apphub.lib.config.common.endpoints;

public class EncryptionEndpoints {
    public static final String ENCRYPTION_INTERNAL_CREATE_KEY = "/internal/encryption/key/{accessMode}";
    public static final String ENCRYPTION_INTERNAL_DELETE_KEY = "/internal/encryption/key/{dataType}/{externalId}/{accessMode}";
    public static final String ENCRYPTION_INTERNAL_GET_KEY = "/internal/encryption/key/{dataType}/{externalId}/{accessMode}";
    public static final String ENCRYPTION_INTERNAL_GET_SHARED_DATA = "/internal/encryption/shared-data/{dataType}/{externalId}";
    public static final String ENCRYPTION_INTERNAL_CREATE_SHARED_DATA = "/internal/encryption/shared-data";
    public static final String ENCRYPTION_INTERNAL_CLONE_SHARED_DATA = "/internal/encryption/shared-data/{dataType}/{externalId}";
    public static final String ENCRYPTION_INTERNAL_DELETE_SHARED_DATA_ENTITY = "/internal/encryption/shared-date/{sharedDataId}";
    public static final String ENCRYPTION_INTERNAL_DELETE_SHARED_DATA = "/internal/encryption/shared-data/{dataType}/{externalId}";
}

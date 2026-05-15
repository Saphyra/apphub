package com.github.saphyra.apphub.service.platform.storage.dao.stored_file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
class StoredFileEntity {
    private String userId;
    private String storedFileId;
    private String fileName; //Encrypted
    private String size; //Encrypted
    private Long createdAt; //Epoch seconds
    private Long expiration;  //Epoch seconds
    private String storage;

    @DynamoDbPartitionKey
    public String getUserId() {
        return userId;
    }

    @DynamoDbSortKey
    public String getStoredFileId() {
        return storedFileId;
    }
}

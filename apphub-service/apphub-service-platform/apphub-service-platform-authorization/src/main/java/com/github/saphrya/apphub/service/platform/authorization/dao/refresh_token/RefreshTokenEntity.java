package com.github.saphrya.apphub.service.platform.authorization.dao.refresh_token;

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
public class RefreshTokenEntity {
    private String userId;
    private String refreshTokenId;
    private Long issuedAt; //Epoch seconds
    private Long expiration; //Epoch seconds
    private Boolean rememberMe;

    @DynamoDbPartitionKey
    public String getUserId() {
        return userId;
    }

    @DynamoDbSortKey
    public String getRefreshTokenId() {
        return refreshTokenId;
    }
}

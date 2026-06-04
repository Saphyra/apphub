package com.github.saphyra.apphub.lib.dynamodb;

import com.github.saphyra.apphub.lib.common_util.SleepService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Component
@RequiredArgsConstructor
@Getter
public class DynamoDbRepositoryContext {
    private final DynamoDbClient client;
    private final DynamoDbRepositoryConfiguration configuration;
    private final SleepService sleepService;
}

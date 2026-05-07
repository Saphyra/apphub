package com.github.saphyra.apphub.ci.process.local;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.process.ProcessKiller;
import com.github.saphyra.apphub.ci.value.PlatformProperties;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocalDynamoDbStartProcess {
    private final PlatformProperties platformProperties;
    private final ProcessKiller processKiller;
    private final PropertyDao propertyDao;

    @SneakyThrows
    public void startDynamoDb() {
        processKiller.killByPort(platformProperties.getLocalDynamoDbPort());

        String dynamoDbDirectoryLocation = propertyDao.getStringProperty(PropertyName.DYNAMO_DB_LOCAL_DIRECTORY);
        if(isBlank(dynamoDbDirectoryLocation)){
            throw new IllegalStateException("DynamoDB Directory Location is empty");
        }

        List<String> command = List.of(
            "cmd",
            "/c",
            "start",
            "java",
            "-Djava.library.path=%s/DynamDBLocal_lib".formatted(dynamoDbDirectoryLocation),
            "-jar",
            "%s/DynamoDBLocal.jar".formatted(dynamoDbDirectoryLocation),
            "-sharedDb"
        );
        log.info(String.join(" ", command));

        new ProcessBuilder(command)
            .start();
    }
}

package com.github.saphyra.apphub.lib.monitoring;

import com.github.saphyra.apphub.api.admin_panel.model.model.MemoryStatusModel;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
@Slf4j
public class MemoryStatusModelFactory {
    private final DateTimeUtil dateTimeUtil;

    public MemoryStatusModel create(String serviceName) {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long maxMemory = runtime.maxMemory();

        return MemoryStatusModel.builder()
            .service(serviceName)
            .availableMemoryBytes(maxMemory)
            .allocatedMemoryBytes(totalMemory)
            .usedMemoryBytes(totalMemory - freeMemory)
            .epochSeconds(dateTimeUtil.getCurrentTime().toEpochSecond(ZoneOffset.UTC))
            .build();
    }
}

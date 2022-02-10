package com.github.saphyra.apphub.lib.monitoring;

import com.github.saphyra.apphub.api.admin_panel.model.model.MemoryStatusModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class MemoryStatusModelFactory {
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
            .build();
    }
}

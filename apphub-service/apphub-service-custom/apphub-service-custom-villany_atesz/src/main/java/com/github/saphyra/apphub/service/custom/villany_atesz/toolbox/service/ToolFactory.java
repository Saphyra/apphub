package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.CreateToolRequest;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolStatus;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class ToolFactory {
    private final IdGenerator idGenerator;
    private final ToolTypeFactory toolTypeFactory;
    private final StorageBoxFactory storageBoxFactory;

    Tool create(UUID userId, CreateToolRequest request) {
        return Tool.builder()
            .toolId(idGenerator.randomUuid())
            .userId(userId)
            .storageBoxId(storageBoxFactory.getStorageBoxId(userId, request.getStorageBox()))
            .toolTypeId(toolTypeFactory.getToolTypeId(userId, request.getToolType()))
            .brand(request.getBrand())
            .name(request.getName())
            .cost(request.getCost())
            .acquiredAt(request.getAcquiredAt())
            .status(ToolStatus.DEFAULT)
            .warrantyExpiresAt(request.getWarrantyExpiresAt())
            .build();
    }
}

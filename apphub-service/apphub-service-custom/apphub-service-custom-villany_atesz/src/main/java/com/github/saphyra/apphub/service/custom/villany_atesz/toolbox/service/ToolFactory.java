package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.CreateToolRequest;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.StorageBoxModel;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolStatus;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolTypeModel;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.storage_box.StorageBox;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.storage_box.StorageBoxDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool.Tool;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool_type.ToolType;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool_type.ToolTypeDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static io.micrometer.common.util.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
@Slf4j
class ToolFactory {
    private final IdGenerator idGenerator;
    private final ToolTypeDao toolTypeDao;
    private final StorageBoxDao storageBoxDao;

    Tool create(UUID userId, CreateToolRequest request) {
        return Tool.builder()
            .toolId(idGenerator.randomUuid())
            .userId(userId)
            .storageBoxId(getStorageBoxId(userId, request.getStorageBox()))
            .toolTypeId(getToolTypeId(userId, request.getToolType()))
            .brand(request.getBrand())
            .name(request.getName())
            .cost(request.getCost())
            .acquiredAt(request.getAcquiredAt())
            .status(ToolStatus.DEFAULT)
            .warrantyExpiresAt(request.getWarrantyExpiresAt())
            .build();
    }

    private UUID getStorageBoxId(UUID userId, StorageBoxModel storageBox) {
        return Optional.ofNullable(storageBox.getStorageBoxId())
            .orElseGet(() -> createStorageBox(userId, storageBox.getName()));
    }

    private UUID createStorageBox(UUID userId, String name) {
        if (isBlank(name)) {
            return null;
        }

        log.info("Creating new storageBox for userId {}", userId);

        StorageBox storageBox = StorageBox.builder()
            .storageBoxId(idGenerator.randomUuid())
            .userId(userId)
            .name(name)
            .build();

        storageBoxDao.save(storageBox);

        return storageBox.getStorageBoxId();
    }

    private UUID getToolTypeId(UUID userId, ToolTypeModel toolType) {
        return Optional.ofNullable(toolType.getToolTypeId())
            .orElseGet(() -> createToolType(userId, toolType.getName()));
    }

    private UUID createToolType(UUID userId, String name) {
        if (isBlank(name)) {
            return null;
        }

        log.info("Creating new toolType for userId {}", userId);

        ToolType toolType = ToolType.builder()
            .toolTypeId(idGenerator.randomUuid())
            .userId(userId)
            .name(name)
            .build();

        toolTypeDao.save(toolType);

        return toolType.getToolTypeId();
    }
}

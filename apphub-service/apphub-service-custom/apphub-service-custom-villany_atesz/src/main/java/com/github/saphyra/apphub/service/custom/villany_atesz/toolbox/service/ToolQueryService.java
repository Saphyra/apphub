package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StorageBoxModel;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolTypeModel;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.storage_box.StorageBoxDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool.Tool;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool.ToolDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool_type.ToolTypeDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ToolQueryService {
    private final ToolDao toolDao;
    private final ToolTypeDao toolTypeDao;
    private final StorageBoxDao storageBoxDao;

    public List<ToolResponse> getTools(UUID userId) {
        return toolDao.getByUserId(userId)
            .stream()
            .map(tool -> ToolResponse.builder()
                .toolId(tool.getToolId())
                .storageBox(getStorageBox(tool.getStorageBoxId()))
                .toolType(getToolType(tool.getToolTypeId()))
                .brand(tool.getBrand())
                .name(tool.getName())
                .cost(tool.getCost())
                .acquiredAt(tool.getAcquiredAt())
                .warrantyExpiresAt(tool.getWarrantyExpiresAt())
                .status(tool.getStatus())
                .scrappedAt(tool.getScrappedAt())
                .inventoried(tool.isInventoried())
                .build())
            .collect(Collectors.toList());
    }

    public ToolTypeModel getToolType(UUID toolTypeId) {
        return Optional.ofNullable(toolTypeId)
            .map(uuid -> toolTypeDao.findByIdValidated(toolTypeId))
            .map(storageBox -> ToolTypeModel.builder()
                .toolTypeId(storageBox.getToolTypeId())
                .name(storageBox.getName())
                .build())
            .orElse(null);
    }

    public StorageBoxModel getStorageBox(UUID storageBoxId) {
        return Optional.ofNullable(storageBoxId)
            .map(uuid -> storageBoxDao.findByIdValidated(storageBoxId))
            .map(storageBox -> StorageBoxModel.builder()
                .storageBoxId(storageBox.getStorageBoxId())
                .name(storageBox.getName())
                .build())
            .orElse(null);
    }

    public int getTotalValue(UUID userId) {
        return toolDao.getByUserId(userId)
            .stream()
            .mapToInt(Tool::getCost)
            .sum();
    }

    public List<ToolTypeModel> getToolTypes(UUID userId) {
        return toolTypeDao.getByUserId(userId)
            .stream()
            .map(toolType -> ToolTypeModel.builder()
                .toolTypeId(toolType.getToolTypeId())
                .name(toolType.getName())
                .build())
            .collect(Collectors.toList());
    }

    public List<StorageBoxModel> getStorageBoxes(UUID userId) {
        return storageBoxDao.getByUserId(userId)
            .stream()
            .map(storageBox -> StorageBoxModel.builder()
                .storageBoxId(storageBox.getStorageBoxId())
                .name(storageBox.getName())
                .build())
            .collect(Collectors.toList());
    }
}

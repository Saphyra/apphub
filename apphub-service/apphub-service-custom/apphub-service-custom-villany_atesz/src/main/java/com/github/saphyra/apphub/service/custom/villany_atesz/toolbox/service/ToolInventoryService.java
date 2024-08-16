package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StorageBoxModel;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolStatus;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolTypeModel;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool.Tool;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool.ToolDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ToolInventoryService {
    private final StorageBoxValidator storageBoxValidator;
    private final ToolDao toolDao;
    private final StorageBoxFactory storageBoxFactory;
    private final ToolTypeValidator toolTypeValidator;
    private final ToolTypeFactory toolTypeFactory;

    public UUID editStorageBox(UUID userId, UUID toolId, StorageBoxModel storageBox) {
        storageBoxValidator.validate(storageBox);

        Tool tool = toolDao.findByIdValidated(toolId);

        tool.setStorageBoxId(storageBoxFactory.getStorageBoxId(userId, storageBox));

        toolDao.save(tool);

        return tool.getStorageBoxId();
    }

    public UUID editToolType(UUID userId, UUID toolId, ToolTypeModel toolType) {
        toolTypeValidator.validate(toolType);

        Tool tool = toolDao.findByIdValidated(toolId);

        tool.setToolTypeId(toolTypeFactory.getToolTypeId(userId, toolType));

        toolDao.save(tool);

        return tool.getToolTypeId();
    }

    public void editBrand(UUID toolId, String brand) {
        ValidationUtil.notNull(brand, "brand");

        Tool tool = toolDao.findByIdValidated(toolId);

        tool.setBrand(brand);

        toolDao.save(tool);
    }

    public void editName(UUID toolId, String name) {
        ValidationUtil.notBlank(name, "name");

        Tool tool = toolDao.findByIdValidated(toolId);

        tool.setName(name);

        toolDao.save(tool);
    }

    public void editCost(UUID toolId, Integer cost) {
        ValidationUtil.notNull(cost, "cost");

        Tool tool = toolDao.findByIdValidated(toolId);

        tool.setCost(cost);

        toolDao.save(tool);
    }

    public void editAcquiredAt(UUID toolId, LocalDate acquiredAt) {
        ValidationUtil.notNull(acquiredAt, "acquiredAt");

        Tool tool = toolDao.findByIdValidated(toolId);

        tool.setAcquiredAt(acquiredAt);

        toolDao.save(tool);
    }

    public void editWarrantyExpiresAt(UUID toolId, LocalDate warrantyExpiresAt) {
        Tool tool = toolDao.findByIdValidated(toolId);

        tool.setWarrantyExpiresAt(warrantyExpiresAt);

        toolDao.save(tool);
    }

    public void editStatus(UUID toolId, ToolStatus status) {
        ValidationUtil.notNull(status, "status");

        Tool tool = toolDao.findByIdValidated(toolId);

        tool.setStatus(status);

        toolDao.save(tool);
    }

    public void editScrappedAt(UUID toolId, LocalDate scrappedAt) {
        Tool tool = toolDao.findByIdValidated(toolId);

        tool.setScrappedAt(scrappedAt);

        toolDao.save(tool);
    }

    public void editInventoried(UUID toolId, Boolean inventoried) {
        ValidationUtil.notNull(inventoried, "inventoried");

        Tool tool = toolDao.findByIdValidated(toolId);

        tool.setInventoried(inventoried);

        toolDao.save(tool);
    }

    public void resetInventoried(UUID userId) {
        List<Tool> tools = toolDao.getByUserId(userId);

        tools.forEach(tool -> tool.setInventoried(false));

        toolDao.saveAll(tools);
    }
}

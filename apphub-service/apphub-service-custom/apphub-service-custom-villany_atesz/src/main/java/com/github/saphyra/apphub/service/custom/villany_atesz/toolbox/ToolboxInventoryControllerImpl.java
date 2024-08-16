package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StorageBoxModel;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolStatus;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolTypeModel;
import com.github.saphyra.apphub.api.custom.villany_atesz.server.ToolboxInventoryController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service.ToolInventoryService;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service.ToolQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
class ToolboxInventoryControllerImpl implements ToolboxInventoryController {
    private final ToolInventoryService toolInventoryService;
    private final ToolQueryService toolQueryService;

    @Override
    public StorageBoxModel editStorageBox(StorageBoxModel storageBox, UUID toolId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit the storageBox of tool {}", accessTokenHeader.getUserId(), toolId);

        UUID storageBoxId = toolInventoryService.editStorageBox(accessTokenHeader.getUserId(), toolId, storageBox);

        return toolQueryService.getStorageBox(storageBoxId);
    }

    @Override
    public ToolTypeModel editToolType(ToolTypeModel toolType, UUID toolId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit the toolType of tool {}", accessTokenHeader.getUserId(), toolId);

        UUID toolTypeId = toolInventoryService.editToolType(accessTokenHeader.getUserId(), toolId, toolType);

        return toolQueryService.getToolType(toolTypeId);
    }

    @Override
    public void editBrand(OneParamRequest<String> brand, UUID toolId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit the brand of tool {}", accessTokenHeader.getUserId(), toolId);

        toolInventoryService.editBrand(toolId, brand.getValue());
    }

    @Override
    public void editName(OneParamRequest<String> name, UUID toolId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit the name of tool {}", accessTokenHeader.getUserId(), toolId);

        toolInventoryService.editName(toolId, name.getValue());
    }

    @Override
    public void editCost(OneParamRequest<Integer> cost, UUID toolId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit the cost of tool {}", accessTokenHeader.getUserId(), toolId);

        toolInventoryService.editCost(toolId, cost.getValue());
    }

    @Override
    public void editAcquiredAt(OneParamRequest<LocalDate> acquiredAt, UUID toolId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit the acquiredAt of tool {}", accessTokenHeader.getUserId(), toolId);

        toolInventoryService.editAcquiredAt(toolId, acquiredAt.getValue());
    }

    @Override
    public void editWarrantyExpiresAt(OneParamRequest<LocalDate> warrantyExpiresAt, UUID toolId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit the warrantyExpiresAt of tool {}", accessTokenHeader.getUserId(), toolId);

        toolInventoryService.editWarrantyExpiresAt(toolId, warrantyExpiresAt.getValue());
    }

    @Override
    public void editStatus(OneParamRequest<ToolStatus> status, UUID toolId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit the status of tool {}", accessTokenHeader.getUserId(), toolId);

        toolInventoryService.editStatus(toolId, status.getValue());
    }

    @Override
    public void editScrappedAt(OneParamRequest<LocalDate> scrappedAt, UUID toolId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit the scrappedAt of tool {}", accessTokenHeader.getUserId(), toolId);

        toolInventoryService.editScrappedAt(toolId, scrappedAt.getValue());
    }

    @Override
    public void editInventoried(OneParamRequest<Boolean> inventoried, UUID toolId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit the inventoried of tool {}", accessTokenHeader.getUserId(), toolId);

        toolInventoryService.editInventoried(toolId, inventoried.getValue());
    }

    @Override
    public List<ToolResponse> resetInventoried(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to reset the inventoried statuses of their tools.}", accessTokenHeader.getUserId());

        toolInventoryService.resetInventoried(accessTokenHeader.getUserId());

        return toolQueryService.getTools(accessTokenHeader.getUserId());
    }
}

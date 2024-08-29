package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.CreateToolRequest;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.StorageBoxModel;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolStatus;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolTypeModel;
import com.github.saphyra.apphub.api.custom.villany_atesz.server.ToolboxController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service.CreateToolService;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service.DeleteToolService;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service.SetToolStatusService;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service.StorageBoxService;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service.ToolQueryService;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service.ToolTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ToolboxControllerImpl implements ToolboxController {
    private final ToolQueryService toolQueryService;
    private final CreateToolService createToolService;
    private final SetToolStatusService setToolStatusService;
    private final DeleteToolService deleteToolService;
    private final ToolTypeService toolTypeService;
    private final StorageBoxService storageBoxService;

    @Override
    public List<ToolResponse> getTools(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know their tools", accessTokenHeader.getUserId());

        return toolQueryService.getTools(accessTokenHeader.getUserId());
    }

    @Override
    public void createTool(CreateToolRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create a tool", accessTokenHeader.getUserId());

        createToolService.create(accessTokenHeader.getUserId(), request);
    }

    @Override
    public List<ToolResponse> setToolStatus(OneParamRequest<ToolStatus> status, UUID toolId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to set status of tool {}", accessTokenHeader.getUserId(), toolId);

        setToolStatusService.setStatus(toolId, status.getValue());

        return getTools(accessTokenHeader);
    }

    @Override
    public List<ToolResponse> deleteTool(UUID toolId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete tool {}", accessTokenHeader.getUserId(), toolId);

        deleteToolService.deleteTool(accessTokenHeader.getUserId(), toolId);

        return getTools(accessTokenHeader);
    }

    @Override
    public List<ToolTypeModel> getToolTypes(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know their toolTypes", accessTokenHeader.getUserId());

        return toolQueryService.getToolTypes(accessTokenHeader.getUserId());
    }

    @Override
    public List<StorageBoxModel> getStorageBoxes(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know their storageBoxes", accessTokenHeader.getUserId());

        return toolQueryService.getStorageBoxes(accessTokenHeader.getUserId());
    }

    @Override
    public void editToolType(OneParamRequest<String> name, UUID toolTypeId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit toolType {}", accessTokenHeader.getUserId(), toolTypeId);
        toolTypeService.edit(toolTypeId, name.getValue());
    }

    @Override
    public void deleteToolType(UUID toolTypeId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete toolType {}", accessTokenHeader.getUserId(), toolTypeId);

        toolTypeService.delete(accessTokenHeader.getUserId(), toolTypeId);
    }

    @Override
    public void editStorageBox(OneParamRequest<String> name, UUID storageBoxId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit storageBox {}", accessTokenHeader.getUserId(), storageBoxId);

        storageBoxService.edit(storageBoxId, name.getValue());
    }

    @Override
    public void deleteStorageBox(UUID storageBoxId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete storageBox {}", accessTokenHeader.getUserId(), storageBoxId);

        storageBoxService.delete(accessTokenHeader.getUserId(), storageBoxId);
    }
}

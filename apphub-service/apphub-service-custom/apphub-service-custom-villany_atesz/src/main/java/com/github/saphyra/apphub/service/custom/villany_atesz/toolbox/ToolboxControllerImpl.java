package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.CreateToolRequest;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolStatus;
import com.github.saphyra.apphub.api.custom.villany_atesz.server.ToolboxController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service.CreateToolService;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service.DeleteToolService;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service.SetToolStatusService;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service.ToolQueryService;
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
}

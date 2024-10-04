package com.github.saphyra.apphub.api.custom.villany_atesz.server;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.CreateToolRequest;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.StorageBoxModel;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolStatus;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolTypeModel;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.config.common.endpoints.VillanyAteszEndpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

public interface ToolboxController {
    @GetMapping(VillanyAteszEndpoints.VILLANY_ATESZ_GET_TOOLS)
    List<ToolResponse> getTools(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PutMapping(VillanyAteszEndpoints.VILLANY_ATESZ_CREATE_TOOL)
    void createTool(@RequestBody CreateToolRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(VillanyAteszEndpoints.VILLANY_ATESZ_SET_TOOL_STATUS)
    List<ToolResponse> setToolStatus(@RequestBody OneParamRequest<ToolStatus> status, @PathVariable("toolId") UUID toolId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(VillanyAteszEndpoints.VILLANY_ATESZ_DELETE_TOOL)
    List<ToolResponse> deleteTool(@PathVariable("toolId") UUID toolId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(VillanyAteszEndpoints.VILLANY_ATESZ_GET_TOOL_TYPES)
    List<ToolTypeModel> getToolTypes(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(VillanyAteszEndpoints.VILLANY_ATESZ_GET_STORAGE_BOXES)
    List<StorageBoxModel> getStorageBoxes(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(VillanyAteszEndpoints.VILLANY_ATESZ_EDIT_TOOL_TYPE)
    void editToolType(@RequestBody OneParamRequest<String> name, @PathVariable("toolTypeId") UUID toolTypeId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(VillanyAteszEndpoints.VILLANY_ATESZ_DELETE_TOOL_TYPE)
    void deleteToolType(@PathVariable("toolTypeId") UUID toolTypeId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(VillanyAteszEndpoints.VILLANY_ATESZ_EDIT_STORAGE_BOX)
    void editStorageBox(@RequestBody OneParamRequest<String> name, @PathVariable("storageBoxId") UUID storageBoxId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(VillanyAteszEndpoints.VILLANY_ATESZ_DELETE_STORAGE_BOX)
    void deleteStorageBox(@PathVariable("storageBoxId") UUID storageBoxId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}

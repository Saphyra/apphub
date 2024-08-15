package com.github.saphyra.apphub.api.custom.villany_atesz.server;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StorageBoxModel;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolStatus;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolTypeModel;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ToolboxInventoryController {
    @PostMapping(Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_STORAGE_BOX)
    StorageBoxModel editStorageBox(@RequestBody StorageBoxModel storageBox, @PathVariable("toolId") UUID toolId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_TOOL_TYPE)
    ToolTypeModel editToolType(@RequestBody ToolTypeModel toolType, @PathVariable("toolId") UUID toolId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_BRAND)
    void editBrand(@RequestBody OneParamRequest<String> brand, @PathVariable("toolId") UUID toolId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_NAME)
    void editName(@RequestBody OneParamRequest<String> name, @PathVariable("toolId") UUID toolId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_COST)
    void editCost(@RequestBody OneParamRequest<Integer> cost, @PathVariable("toolId") UUID toolId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_ACQUIRED_AT)
    void editAcquiredAt(@RequestBody OneParamRequest<LocalDate> acquiredAt, @PathVariable("toolId") UUID toolId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_WARRANTY_EXPIRES_AT)
    void editWarrantyExpiresAt(@RequestBody OneParamRequest<LocalDate> warrantyExpiresAt, @PathVariable("toolId") UUID toolId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_STATUS)
    void editStatus(@RequestBody OneParamRequest<ToolStatus> status, @PathVariable("toolId") UUID toolId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_SCRAPPED_AT)
    void editScrappedAt(@RequestBody OneParamRequest<LocalDate> scrappedAt, @PathVariable("toolId") UUID toolId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_INVENTORIED)
    void editInventoried(@RequestBody OneParamRequest<Boolean> inventoried, @PathVariable("toolId") UUID toolId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_RESET_INVENTORIED)
    List<ToolResponse> resetInventoried(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}

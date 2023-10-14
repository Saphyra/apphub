package com.github.saphyra.apphub.api.notebook.server;

import com.github.saphyra.apphub.api.notebook.model.request.CustomTableRequest;
import com.github.saphyra.apphub.api.notebook.model.response.CustomTableCreatedResponse;
import com.github.saphyra.apphub.api.notebook.model.response.CustomTableResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

//TODO API test
@Deprecated
public interface CustomTableControllerDeprecated {
    @PutMapping(Endpoints.NOTEBOOK_CREATE_CUSTOM_TABLE)
    List<CustomTableCreatedResponse> createCustomTable(@RequestBody CustomTableRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.NOTEBOOK_EDIT_CUSTOM_TABLE)
    List<CustomTableCreatedResponse> editCustomTable(@RequestBody CustomTableRequest request, @PathVariable("listItemId") UUID listItemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(path = Endpoints.NOTEBOOK_GET_CUSTOM_TABLE)
    CustomTableResponse getCustomTable(@PathVariable("listItemId") UUID listItemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(path = Endpoints.NOTEBOOK_UPDATE_CUSTOM_TABLE_ROW_STATUS)
    void setCustomTableRowStatus(@PathVariable("rowId") UUID rowId, @RequestBody OneParamRequest<Boolean> status, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(Endpoints.NOTEBOOK_DELETE_CHECKED_ITEMS_FROM_CUSTOM_TABLE)
    void deleteCheckedItems(@PathVariable("listItemId") UUID listItemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}

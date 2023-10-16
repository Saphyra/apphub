package com.github.saphyra.apphub.api.notebook.server;

import com.github.saphyra.apphub.api.notebook.model.table.CreateTableRequest;
import com.github.saphyra.apphub.api.notebook.model.table.EditTableRequest;
import com.github.saphyra.apphub.api.notebook.model.table.EditTableResponse;
import com.github.saphyra.apphub.api.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.api.notebook.model.table.TableResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.UUID;

public interface TableController {
    @RequestMapping(method = RequestMethod.PUT, path = Endpoints.NOTEBOOK_CREATE_TABLE)
    List<TableFileUploadResponse> createTable(@RequestBody CreateTableRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @RequestMapping(method = RequestMethod.POST, path = Endpoints.NOTEBOOK_EDIT_TABLE)
    EditTableResponse editTable(@RequestBody EditTableRequest request, @PathVariable(name = "listItemId") UUID listItemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @RequestMapping(method = RequestMethod.GET, path = Endpoints.NOTEBOOK_GET_TABLE)
    TableResponse getTable(@PathVariable("listItemId") UUID listItemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(path = Endpoints.NOTEBOOK_TABLE_SET_ROW_STATUS)
    void setRowStatus(@PathVariable("rowId") UUID rowId, @RequestBody OneParamRequest<Boolean> status, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(Endpoints.NOTEBOOK_TABLE_DELETE_CHECKED)
    TableResponse deleteCheckedRows(@PathVariable("listItemId") UUID listItemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}

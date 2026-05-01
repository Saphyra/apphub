package com.github.saphyra.apphub.api.feature.notebook.server;

import com.github.saphyra.apphub.api.feature.notebook.model.request.CreateFileRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.api.feature.notebook.model.NotebookEndpoints;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

public interface FileController {
    @PutMapping(NotebookEndpoints.NOTEBOOK_CREATE_FILE)
    OneParamResponse<UUID> createFile(@RequestBody CreateFileRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessToken accessToken);
}

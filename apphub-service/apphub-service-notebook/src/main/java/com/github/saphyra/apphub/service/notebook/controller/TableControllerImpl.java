package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.CreateTableRequest;
import com.github.saphyra.apphub.api.notebook.server.TableController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.notebook.service.table.creation.TableCreationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
public class TableControllerImpl implements TableController {
    private final TableCreationService tableCreationService;

    @Override
    //TODO unit test
    //TODO int test
    //TODO api test
    //TODO test user deletion
    public OneParamResponse<UUID> createTable(CreateTableRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create a table.", accessTokenHeader.getUserId());
        return new OneParamResponse<>(tableCreationService.create(request, accessTokenHeader.getUserId()));
    }
}

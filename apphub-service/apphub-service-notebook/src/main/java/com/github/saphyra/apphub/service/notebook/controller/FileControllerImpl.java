package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.CreateFileRequest;
import com.github.saphyra.apphub.api.notebook.server.FileController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.notebook.service.file.FileCreationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class FileControllerImpl implements FileController {
    private final FileCreationService fileCreationService;

    @Override
    public OneParamResponse<UUID> createFile(CreateFileRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create a file", accessTokenHeader.getUserId());
        return new OneParamResponse<>(fileCreationService.createFile(accessTokenHeader.getUserId(), request));
    }
}

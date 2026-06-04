package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.request.CreateFileRequest;
import com.github.saphyra.apphub.api.feature.notebook.server.FileController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.notebook.service.file.FileCreationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FileControllerImpl implements FileController {
    private final FileCreationService fileCreationService;

    @Override
    public OneParamResponse<UUID> createFile(CreateFileRequest request, AccessToken accessToken) {
        log.info("{} wants to create a file", accessToken.getUserId());
        return new OneParamResponse<>(fileCreationService.create(accessToken.getUserId(), request, ListItemType.FILE));
    }
}

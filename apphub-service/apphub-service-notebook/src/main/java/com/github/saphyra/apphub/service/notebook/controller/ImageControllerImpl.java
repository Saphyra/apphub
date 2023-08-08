package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.CreateFileRequest;
import com.github.saphyra.apphub.api.notebook.server.ImageController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.notebook.service.image.ImageCreationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ImageControllerImpl implements ImageController {
    private final ImageCreationService imageCreationService;

    @Override
    public OneParamResponse<UUID> createImage(CreateFileRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create an image", accessTokenHeader.getUserId());
        return new OneParamResponse<>(imageCreationService.createImage(accessTokenHeader.getUserId(), request));
    }
}

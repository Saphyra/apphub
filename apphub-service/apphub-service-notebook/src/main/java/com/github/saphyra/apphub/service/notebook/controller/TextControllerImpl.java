package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.CreateTextRequest;
import com.github.saphyra.apphub.api.notebook.server.TextController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.notebook.service.text.creation.TextCreationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
class TextControllerImpl implements TextController {
    private final TextCreationService textCreationService;

    @Override
    //TODO unit test
    //TODO int test
    //TODO api test
    public OneParamResponse<UUID> createText(CreateTextRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create a new text item with parentId {}", accessTokenHeader.getUserId(), request.getParent());
        return new OneParamResponse<>(textCreationService.create(request, accessTokenHeader.getUserId()));
    }
}

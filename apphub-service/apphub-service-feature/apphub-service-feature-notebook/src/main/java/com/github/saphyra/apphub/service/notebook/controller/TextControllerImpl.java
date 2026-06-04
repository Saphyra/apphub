package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.feature.notebook.model.request.CreateTextRequest;
import com.github.saphyra.apphub.api.feature.notebook.model.request.EditTextRequest;
import com.github.saphyra.apphub.api.feature.notebook.model.response.TextResponse;
import com.github.saphyra.apphub.api.feature.notebook.server.TextController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.notebook.service.text.EditTextService;
import com.github.saphyra.apphub.service.notebook.service.text.TextQueryService;
import com.github.saphyra.apphub.service.notebook.service.text.TextCreationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
class TextControllerImpl implements TextController {
    private final EditTextService editTextService;
    private final TextCreationService textCreationService;
    private final TextQueryService textQueryService;

    @Override
    public OneParamResponse<UUID> createText(CreateTextRequest request, AccessToken accessToken) {
        log.info("{} wants to create a new text item with parentId {}", accessToken.getUserId(), request.getParent());
        return new OneParamResponse<>(textCreationService.create(request, accessToken.getUserId()));
    }

    @Override
    public TextResponse getText(UUID listItemId, AccessToken accessToken) {
        log.info("{} wants to query text with id {}", accessToken.getUserId(), listItemId);
        return textQueryService.getTextResponse(accessToken.getUserId(), listItemId);
    }

    @Override
    public void editText(EditTextRequest request, UUID listItemId, AccessToken accessToken) {
        log.info("{} wants to edit text with id {}", accessToken.getUserId(), listItemId);
        editTextService.editText(accessToken.getUserId(), listItemId, request);
    }
}

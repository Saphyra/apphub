package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.CreateTextRequest;
import com.github.saphyra.apphub.api.notebook.model.request.EditTextRequest;
import com.github.saphyra.apphub.api.notebook.model.response.TextResponse;
import com.github.saphyra.apphub.api.notebook.server.TextController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.notebook.service.text.EditTextService;
import com.github.saphyra.apphub.service.notebook.service.text.TextQueryService;
import com.github.saphyra.apphub.service.notebook.service.text.creation.TextCreationService;
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
    //TODO unit test
    //TODO int test
    //TODO api test
    public OneParamResponse<UUID> createText(CreateTextRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create a new text item with parentId {}", accessTokenHeader.getUserId(), request.getParent());
        return new OneParamResponse<>(textCreationService.create(request, accessTokenHeader.getUserId()));
    }

    @Override
    //TODO unit test
    //TODO int test
    //TODO api test
    public TextResponse getText(UUID textId) {
        log.info("Querying text with id {}", textId);
        return textQueryService.getTextResponse(textId);
    }

    @Override
    public void editTextContent(EditTextRequest request, UUID textId) {
        log.info("Editing text with id {}", textId);
        editTextService.editText(textId, request);
    }
}

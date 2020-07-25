package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.LinkRequest;
import com.github.saphyra.apphub.api.notebook.server.LinkController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.notebook.service.link.LinkCreationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@Slf4j
public class LinkControllerImpl implements LinkController {
    private final LinkCreationService linkCreationService;

    @Override
    //TODO unit test
    //TODO int test
    //TODO API test
    public OneParamResponse<UUID> createLink(LinkRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("Creating new link for user {}", accessTokenHeader.getUserId());
        return new OneParamResponse<>(linkCreationService.create(request, accessTokenHeader.getUserId()));
    }
}

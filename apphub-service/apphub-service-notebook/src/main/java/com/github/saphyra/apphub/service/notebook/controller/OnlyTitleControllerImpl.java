package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.CreateOnlyTitleRequest;
import com.github.saphyra.apphub.api.notebook.server.OnlyTitleController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.notebook.service.only_title.OnlyTitleCreationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OnlyTitleControllerImpl implements OnlyTitleController {
    private final OnlyTitleCreationService onlyTitleCreationService;

    @Override
    public OneParamResponse<UUID> createOnlyTitle(CreateOnlyTitleRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create an only-title item.", accessTokenHeader.getUserId());
        return new OneParamResponse<>(onlyTitleCreationService.create(request, accessTokenHeader.getUserId()));
    }
}

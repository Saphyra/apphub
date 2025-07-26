package com.github.saphyra.apphub.service.calendar.domain.label;

import com.github.saphyra.apphub.api.calendar.model.response.LabelResponse;
import com.github.saphyra.apphub.api.calendar.server.LabelController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.calendar.domain.label.service.LabelQueryService;
import com.github.saphyra.apphub.service.calendar.domain.label.service.LabelService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class LabelControllerImpl implements LabelController {
    private final LabelQueryService labelQueryService;
    private final LabelService labelService;

    @Override
    public void createLabel(OneParamRequest<String> label, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create a new label.", accessTokenHeader.getUserId());

        labelService.createLabel(accessTokenHeader.getUserId(), label.getValue());
    }

    @Override
    public List<LabelResponse> getLabels(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to get labels.", accessTokenHeader.getUserId());

        return labelQueryService.getByUserId(accessTokenHeader.getUserId());
    }

    @Override
    @Transactional
    public void deleteLabel(UUID labelId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete label {}.", accessTokenHeader.getUserId(), labelId);

        labelService.deleteLabel(accessTokenHeader.getUserId(), labelId);
    }

    @Override
    public void editLabel(OneParamRequest<String> label, UUID labelId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit label {}", accessTokenHeader.getUserId(), labelId);

        labelService.editLabel(accessTokenHeader.getUserId(), labelId, label.getValue());
    }
}

package com.github.saphyra.apphub.service.calendar.domain.label;

import com.github.saphyra.apphub.api.calendar.model.response.LabelResponse;
import com.github.saphyra.apphub.api.calendar.server.LabelController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.calendar.domain.label.service.LabelQueryService;
import com.github.saphyra.apphub.service.calendar.domain.label.service.LabelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
class LabelControllerImpl implements LabelController {
    private final LabelQueryService labelQueryService;
    private final LabelService labelService;

    @Override
    public OneParamResponse<UUID> createLabel(OneParamRequest<String> label, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create a new label.", accessTokenHeader.getUserId());
        log.debug(label.toString());

        UUID labelId = labelService.createLabel(accessTokenHeader.getUserId(), label.getValue());
        OneParamResponse<UUID> response = new OneParamResponse<>(labelId);
        log.debug("Response: {}", response);

        return response;
    }

    @Override
    public List<LabelResponse> getLabels(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to get labels.", accessTokenHeader.getUserId());

        List<LabelResponse> response = labelQueryService.getByUserId(accessTokenHeader.getUserId());
        log.debug("Response: {}", response);

        return response;
    }

    @Override
    public LabelResponse getLabel(UUID labelId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to get label {}.", accessTokenHeader.getUserId(), labelId);

        LabelResponse response = labelQueryService.getLabel(labelId);
        log.debug("Response: {}", response);

        return response;
    }

    @Override
    public List<LabelResponse> deleteLabel(UUID labelId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete label {}.", accessTokenHeader.getUserId(), labelId);

        labelService.deleteLabel(accessTokenHeader.getUserId(), labelId);

        return getLabels(accessTokenHeader);
    }

    @Override
    public List<LabelResponse> editLabel(OneParamRequest<String> label, UUID labelId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit label {}", accessTokenHeader.getUserId(), labelId);
        log.debug(label.toString());

        labelService.editLabel(accessTokenHeader.getUserId(), labelId, label.getValue());

        return getLabels(accessTokenHeader);
    }

    @Override
    public List<LabelResponse> getLabelsOfEvent(UUID eventId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to get labels of event {}.", accessTokenHeader.getUserId(), eventId);

        return labelQueryService.getByEventId(eventId);
    }
}

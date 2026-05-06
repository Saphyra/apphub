package com.github.saphyra.apphub.service.feature.calendar.domain.label;

import com.github.saphyra.apphub.api.feature.calendar.model.response.LabelResponse;
import com.github.saphyra.apphub.api.feature.calendar.server.LabelController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.service.LabelQueryService;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.service.LabelService;
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
    public OneParamResponse<UUID> createLabel(OneParamRequest<String> label, AccessToken accessToken) {
        log.info("{} wants to create a new label.", accessToken.getUserId());
        log.debug(label.toString());

        UUID labelId = labelService.createLabel(accessToken.getUserId(), label.getValue());
        OneParamResponse<UUID> response = new OneParamResponse<>(labelId);
        log.debug("Response: {}", response);

        return response;
    }

    @Override
    public List<LabelResponse> getLabels(AccessToken accessToken) {
        log.info("{} wants to get labels.", accessToken.getUserId());

        List<LabelResponse> response = labelQueryService.getByUserId(accessToken.getUserId());
        log.debug("Response: {}", response);

        return response;
    }

    @Override
    public LabelResponse getLabel(UUID labelId, AccessToken accessToken) {
        log.info("{} wants to get label {}.", accessToken.getUserId(), labelId);

        LabelResponse response = labelQueryService.getLabel(labelId);
        log.debug("Response: {}", response);

        return response;
    }

    @Override
    public List<LabelResponse> deleteLabel(UUID labelId, AccessToken accessToken) {
        log.info("{} wants to delete label {}.", accessToken.getUserId(), labelId);

        labelService.deleteLabel(accessToken.getUserId(), labelId);

        return getLabels(accessToken);
    }

    @Override
    public List<LabelResponse> editLabel(OneParamRequest<String> label, UUID labelId, AccessToken accessToken) {
        log.info("{} wants to edit label {}", accessToken.getUserId(), labelId);
        log.debug(label.toString());

        labelService.editLabel(accessToken.getUserId(), labelId, label.getValue());

        return getLabels(accessToken);
    }

    @Override
    public List<LabelResponse> getLabelsOfEvent(UUID eventId, AccessToken accessToken) {
        log.info("{} wants to get labels of event {}.", accessToken.getUserId(), eventId);

        return labelQueryService.getByEventId(eventId);
    }
}

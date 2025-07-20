package com.github.saphyra.apphub.service.calendar.domain.label;

import com.github.saphyra.apphub.api.calendar.model.response.LabelResponse;
import com.github.saphyra.apphub.api.calendar.server.LabelController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.calendar.domain.label.dao.Label;
import com.github.saphyra.apphub.service.calendar.domain.label.dao.LabelDao;
import com.github.saphyra.apphub.service.calendar.domain.label.dao.LabelFactory;
import com.github.saphyra.apphub.service.calendar.domain.label.service.LabelQueryService;
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
    private final LabelDao labelDao;
    private final LabelFactory labelFactory;
    private final EventLabelMappingDao eventLabelMappingDao;

    @Override
    public void createLabel(OneParamRequest<String> label, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create a new label.", accessTokenHeader.getUserId());

        ValidationUtil.notBlank(label.getValue(), "label");

        labelDao.save(labelFactory.create(accessTokenHeader.getUserId(), label.getValue()));
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

        eventLabelMappingDao.deleteByUserIdAndLabelId(accessTokenHeader.getUserId(), labelId);
        labelDao.deleteByUserIdAndLabelId(accessTokenHeader.getUserId(), labelId);
    }

    @Override
    public void editLabel(OneParamRequest<String> label, UUID labelId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit label {}", accessTokenHeader.getUserId(), labelId);

        ValidationUtil.notBlank(label.getValue(), "label");

        Label l = labelDao.findByIdValidated(labelId);
        l.setLabel(label.getValue());

        labelDao.save(l);
    }
}

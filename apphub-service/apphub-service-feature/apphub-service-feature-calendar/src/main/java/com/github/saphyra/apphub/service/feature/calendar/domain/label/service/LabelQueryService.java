package com.github.saphyra.apphub.service.feature.calendar.domain.label.service;

import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.api.feature.calendar.model.response.LabelResponse;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.Label;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.LabelDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.Alm;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.PrincipalType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class LabelQueryService {
    private final EventLabelMappingDao eventLabelMappingDao;
    private final LabelDao labelDao;
    private final LabelToResponseMapper labelToResponseMapper;
    private final AlmDao almDao;
    private final AccessTokenProvider accessTokenProvider;

    public List<LabelResponse> getByEventId(UUID userId, UUID eventId) {
        //TODO verify access
        Map<UUID, UUID> labelIds = eventLabelMappingDao.getLabelsOfEvent(userId, eventId)
            .getLabelIds();
        log.info("Labels found for Event {}: {}", eventId, labelIds);
        List<Label> labels = labelDao.getByIds(labelIds);
        return labelToResponseMapper.toResponse(userId, labels);
    }

    public List<LabelResponse> getByUserId(UUID userId) {
        return Stream.concat(
                labelDao.getByUserId(userId).stream().map(label -> new BiWrapper<>(label, false)),
                getByAlm(userId)
            )
            .map(bw -> labelToResponseMapper.toResponse(bw.getEntity1(), bw.getEntity2()))
            .toList();
    }

    //TODO optimize
    private Stream<BiWrapper<Label, Boolean>> getByAlm(UUID userId) {
        return almDao.getByUserIdAndObjectType(userId, SharedObjectType.LABEL)
            .stream()
            .filter(alm -> true) //TODO check permission
            .map(this::queryLabelForAlm)
            .map(label -> new BiWrapper<>(label, true));
    }

    private Label queryLabelForAlm(Alm alm) {
        try (var _ = accessTokenProvider.set(alm.getOwner())) {
            //TODO mask if needed
            return labelDao.findByIdValidated(alm.getOwner(), alm.getObjectId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public LabelResponse getLabel(UUID userId, UUID labelId) {
        return almDao.findForObject(userId, PrincipalType.USER, labelId, SharedObjectType.LABEL)
            .filter(alm -> true) //TODO check permission
            .map(alm -> labelToResponseMapper.toResponse(queryLabelForAlm(alm), true))
            .orElseGet(() -> labelToResponseMapper.toResponse(labelDao.findByIdValidated(userId, labelId), false));
    }
}

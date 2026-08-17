package com.github.saphyra.apphub.service.feature.calendar.domain.label.service;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.Label;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.LabelDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.PrincipalType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class LabelObjectQueryService {
    private final EventLabelMappingDao eventLabelMappingDao;
    private final AlmDao almDao;
    private final LabelDao labelDao;

    public List<Label> getLabelsOfEvent(UUID userId, UUID eventId) {
        //Get labels of event
        List<BiWrapper<UUID, UUID>> labelIds = eventLabelMappingDao.getLabelsOfEvent(userId, eventId)
            .getLabelIds()
            .entrySet()
            .stream()
            .map(e -> new BiWrapper<>(e.getValue(), e.getKey()))
            //Filter for visible labels
            .filter(bw -> bw.getEntity1().equals(userId) || almDao.findForObject(userId, PrincipalType.USER, bw.getEntity2(), SharedObjectType.LABEL).filter(alm -> alm.getGrants().contains(Grant.VIEW)).isPresent())
            .toList();

        return labelDao.getByIds(labelIds);
    }

    public Stream<Label> getByUserId(UUID userId) {
        return Stream.concat(
            labelDao.getByUserId(userId).stream(),
            almDao.getByUserIdAndObjectType(userId, SharedObjectType.LABEL)
                .stream()
                .filter(alm -> alm.getGrants().contains(Grant.VIEW))
                .map(alm -> labelDao.findByIdValidated(alm.getOwner(), alm.getObjectId()))
        );
    }

    public Optional<Label> findLabel(UUID userId, UUID labelId, Grant... requiredGrants) {
        return labelDao.findById(userId, labelId)
            .or(() -> almDao.findForObject(userId, PrincipalType.USER, labelId, SharedObjectType.LABEL)
                .filter(alm -> alm.getGrants().containsAll(Arrays.asList(requiredGrants)))
                .flatMap(alm -> labelDao.findById(alm.getOwner(), alm.getObjectId()))
            );
    }
}

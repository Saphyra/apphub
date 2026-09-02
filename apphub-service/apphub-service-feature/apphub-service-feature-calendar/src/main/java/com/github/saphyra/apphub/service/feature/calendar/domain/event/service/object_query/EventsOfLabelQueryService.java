package com.github.saphyra.apphub.service.feature.calendar.domain.event.service.object_query;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.LabelEventMapping;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.Alm;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.PrincipalType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class EventsOfLabelQueryService {
    private final EventLabelMappingDao eventLabelMappingDao;
    private final AlmDao almDao;
    private final EventDao eventDao;
    private final EventMaskedChecker eventMaskedChecker;

    List<Event> getEventsOfLabel(UUID userId, UUID labelId) {
        Optional<LabelEventMapping> maybeLabelEventMapping = eventLabelMappingDao.getEventsOfLabel(userId, labelId);
        Optional<Alm> maybeAlm = Optional.empty();
        if (maybeLabelEventMapping.isPresent()) {
            log.info("Found: {}", maybeLabelEventMapping.get());
        } else {
            log.info("LabelEventMapping not found for userId {} and labelId {}. Check if label is shared with the user.", userId, labelId);
            maybeAlm = almDao.findForObject(userId, PrincipalType.USER, labelId, SharedObjectType.LABEL)
                .map(alm -> {
                    log.info("Found: {}", alm);

                    return alm;
                })
                .filter(alm -> alm.getGrants().contains(Grant.VIEW_CHILDREN) || alm.getGrants().contains(Grant.SEE_CHILDREN))
                .map(alm -> {
                    log.info("Found matching: {}", alm);

                    return alm;
                });
            maybeLabelEventMapping = maybeAlm.flatMap(alm -> eventLabelMappingDao.getEventsOfLabel(alm.getOwner(), labelId));
        }

        if (maybeLabelEventMapping.isPresent()) {
            LabelEventMapping labelEventMapping = maybeLabelEventMapping.get();
            log.info("Found: {}", labelEventMapping);
            Alm alm = maybeAlm.orElse(null);

            List<BiWrapper<UUID, UUID>> eventIds = labelEventMapping.getEventIds()
                .entrySet()
                .stream()
                .map(e -> new BiWrapper<>(e.getValue(), e.getKey()))
                .toList();

            return eventDao.getByIds(eventIds)
                .stream()
                .map(event -> event.setMasked(eventMaskedChecker.isMasked_labelAlm(userId, event, alm, labelId)))
                .toList();
        } else {
            log.info("LabelEventMapping not found for userId {} and labelId {}. Alm: {}", userId, labelId, maybeAlm);
            return List.of();
        }
    }
}

package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.object_query;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.PrincipalType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class GetOccurrencesOfUserService {
    private final EventGrantFinder eventGrantFinder;
    private final AlmDao almDao;
    private final GetOccurrencesOfUserServiceHelper helper;

    Map<Event, List<Occurrence>> getOccurrences(UUID userId) {
        List<Occurrence> occurrences = helper.getOccurrences(userId);

        return occurrences.stream()
            .map(Occurrence::getEventId)
            .distinct()
            .map(eventId -> eventGrantFinder.getEventWithGrants(userId, eventId))
            .map(bw -> {
                Event event = bw.getEntity1();
                Set<Grant> eventGrants = bw.getEntity2();
                log.info("Grants of event {}: {}", event.getEventId(), eventGrants);

                if (!eventGrants.contains(Grant.VIEW)) {
                    log.info("User {} does not have VIEW grant for event {}, masking event", userId, event.getEventId());
                    event.setMasked(true);
                }

                List<Occurrence> eventOccurrences = occurrences.stream()
                    .filter(occurrence -> occurrence.getEventId().equals(event.getEventId()))
                    .peek(occurrence -> {
                        if (
                            //Own event or shared event allows viewing children
                            !eventGrants.contains(Grant.VIEW_CHILDREN)
                                //Own occurrence
                                && !occurrence.getUserId().equals(userId)
                                //Shared occurrence allows viewing
                                && almDao.findForObject(userId, PrincipalType.USER, occurrence.getOccurrenceId(), SharedObjectType.OCCURRENCE).filter(alm -> alm.getGrants().contains(Grant.VIEW)).isEmpty()
                        ) {
                            log.info("User {} does not have VIEW grant for occurrence {}, masking occurrence", userId, occurrence.getOccurrenceId());
                            occurrence.setMasked(true);
                        }
                    })
                    .toList();

                return new BiWrapper<>(event, eventOccurrences);
            })
            .collect(Collectors.toMap(BiWrapper::getEntity1, BiWrapper::getEntity2));
    }
}

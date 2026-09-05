package com.github.saphyra.apphub.service.feature.calendar.domain.event.service.object_query;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.Label;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.service.LabelObjectQueryService;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.Alm;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.PrincipalType;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
class EventMaskedChecker {
    private final AlmDao almDao;
    private final EventLabelMappingDao eventLabelMappingDao;
    private final LabelObjectQueryService labelObjectQueryService;

    boolean isMasked_labelAlm(UUID userId, Event event, @Nullable Alm labelAlm, UUID labelId) {
        //No masking when own event
        if (userId.equals(event.getUserId())) {
            return false;
        }

        //Label is shared
        if (nonNull(labelAlm)) {
            return !labelAlm.getGrants().contains(Grant.VIEW_CHILDREN)
                //If Label does not allow viewing the event, check if the event is explicitly shared with view grant
                && almDao.findForObject(userId, PrincipalType.USER, event.getEventId(), SharedObjectType.EVENT)
                .filter(eventAlm -> eventAlm.getGrants().contains(Grant.VIEW))
                .isEmpty();
        }


        //Event is not the user's, but the label is owned or shared with the user.
        Optional<BiWrapper<Label, Set<Grant>>> maybeLabelWithGrants = labelObjectQueryService.findLabel(userId, labelId);
        if (maybeLabelWithGrants.isPresent()) {
            return !maybeLabelWithGrants.get()
                .getEntity2()
                .contains(Grant.VIEW_CHILDREN);
        }

        throw new IllegalStateException("Event is not owned by the user, label is not owned by or shared with the user, and Alm is null. UserId: " + userId + " eventId: " + event.getEventId());
    }

    boolean isMasked_eventAlm(UUID userId, Event event, Alm eventAlm) {
        Supplier<Boolean> eventAlmMasking = () -> !eventAlm.getGrants().contains(Grant.VIEW);
        Supplier<Boolean> labelAlmMasking = () -> {
            //Get labels shared with user
            return almDao.getByUserIdAndObjectType(userId, SharedObjectType.LABEL)
                .stream()
                //Filter labels that allow viewing events
                .filter(alm -> alm.getGrants().contains(Grant.VIEW_CHILDREN))
                //Get events for the labels
                .map(alm -> eventLabelMappingDao.getEventsOfLabel(alm.getOwner(), alm.getObjectId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .flatMap(labelEventMapping -> labelEventMapping.getEventIds().keySet().stream())
                .noneMatch(eventId -> eventId.equals(event.getEventId()));
        };

        return eventAlmMasking.get() && labelAlmMasking.get();
    }
}

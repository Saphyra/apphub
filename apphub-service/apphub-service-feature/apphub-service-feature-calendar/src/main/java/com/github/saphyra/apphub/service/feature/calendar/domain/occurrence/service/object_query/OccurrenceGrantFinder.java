package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.object_query;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.Alm;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.PrincipalType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
class OccurrenceGrantFinder {
    private final OccurrenceDao occurrenceDao;
    private final AlmDao almDao;

    /**
     * @return the occurrence with the given id and all grants the user has for it. (All applicable grants if occurrence is user's own)
     */
    BiWrapper<Occurrence, Set<Grant>> getOccurrenceWithGrants(UUID userId, UUID eventId, UUID occurrenceId) {
        Occurrence occurrence = occurrenceDao.findByIdValidated(eventId, occurrenceId);
        Set<Grant> grants;
        if (occurrence.getUserId().equals(userId)) {
            grants = Grant.forType(SharedObjectType.OCCURRENCE);
        } else {
            grants = almDao.findForObject(userId, PrincipalType.USER, occurrenceId, SharedObjectType.OCCURRENCE)
                .map(Alm::getGrants)
                .orElse(Set.of());
        }

        return new BiWrapper<>(occurrence, grants);
    }
}

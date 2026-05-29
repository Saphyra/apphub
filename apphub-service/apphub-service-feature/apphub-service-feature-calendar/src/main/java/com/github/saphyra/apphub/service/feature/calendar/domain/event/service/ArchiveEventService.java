package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEvent;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEventDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ArchiveEventService {
    private final DeprecatedEventDao eventDao;

    public void archive(UUID eventId, Boolean archived) {
        ValidationUtil.notNull(archived, "archived");

        DeprecatedEvent event = eventDao.findByIdValidated(eventId);

        event.setArchived(archived);

        eventDao.save(event);
    }
}

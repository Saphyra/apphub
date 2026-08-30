package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.feature.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.api.feature.calendar.model.response.OccurrenceResponse;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.saphyra.apphub.service.feature.calendar.common.CalendarUtils.mask;
import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
class OccurrenceResponseMapper {
    private final OccurrenceDao occurrenceDao;

    public List<OccurrenceResponse> toResponse(UUID userId, Collection<Event> events, List<Occurrence> occurrences) {
        Map<UUID, Event> eventMapping = events.stream()
            .collect(Collectors.toMap(Event::getEventId, Function.identity()));

        return occurrences.stream()
            .map(occurrence -> toResponse(userId, eventMapping.get(occurrence.getEventId()), occurrence))
            .toList();
    }

    public List<OccurrenceResponse> toResponse(UUID userId, Event event, List<Occurrence> occurrences) {
        return occurrences.stream()
            .map(occurrence -> toResponse(userId, event, occurrence))
            .toList();
    }

    public OccurrenceResponse toResponse(UUID userId, Event event, Occurrence occurrence) {
        Boolean autoDone = getFromEventIfNull(event, occurrence.getAutoDone(), Event::isAutoDone, null);
        if (occurrence.getStatus() == OccurrenceStatus.EXPIRED && nonNull(autoDone) && autoDone) {
            //Despite being masked, record contains the data so it is safe to edit the original record.
            occurrence.setStatus(OccurrenceStatus.DONE);
            occurrenceDao.save(occurrence);
        }

        boolean occurrenceMasked = occurrence.isMasked();
        boolean eventMasked = event.isMasked();

        log.info("Mapping occurrence {}. Event masked: {}, occurrence masked: {}", occurrence.getOccurrenceId(), eventMasked, occurrenceMasked);

        return OccurrenceResponse.builder()
            .occurrenceId(occurrence.getOccurrenceId())
            .eventId(occurrence.getEventId())
            .date(occurrence.getDate())
            .time(getTime(event, occurrence))
            .status(occurrence.getStatus())
            .title(mask(eventMasked, event.getTitle(), Constants.QUESTION_MARK))
            .content(mask(eventMasked, event.getContent(), Constants.EMPTY_STRING))
            .note(mask(occurrenceMasked, occurrence.getNote(), Constants.EMPTY_STRING))
            .remindMeBeforeDays(getFromEventIfNull(event, occurrence.getRemindMeBeforeDays(), Event::getRemindMeBeforeDays, 0))
            .reminded(occurrence.isReminded())
            .eventArchived(event.isArchived())
            .autoDone(autoDone)
            .shared(!userId.equals(occurrence.getUserId()))
            .build();
    }

    private LocalTime getTime(Event event, Occurrence occurrence) {
        Optional<LocalTime> eventTime = Optional.ofNullable(event.getTime())
            .filter(_ -> !event.isMasked());

        Optional<LocalTime> occurrenceTime = Optional.ofNullable(occurrence.getTime())
            .filter(_ -> !occurrence.isMasked());

        return occurrenceTime.or(() -> eventTime)
            .orElse(null);
    }

    private <T> T getFromEventIfNull(Event event, T value, Function<Event, T> mapper, T defaultValue) {
        return Optional.ofNullable(value)
            .orElseGet(() -> mask(event.isMasked(), mapper.apply(event), defaultValue));
    }
}

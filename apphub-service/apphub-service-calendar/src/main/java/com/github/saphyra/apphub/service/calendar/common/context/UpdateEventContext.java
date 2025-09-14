package com.github.saphyra.apphub.service.calendar.common.context;

import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.OccurrenceDao;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.service.RecreateOccurrenceService;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import static java.util.Objects.isNull;

@Slf4j
// TODO unit test
public class UpdateEventContext {
    @Getter
    private final Event event;

    private final EventDao eventDao;
    private final OccurrenceDao occurrenceDao;
    private final RecreateOccurrenceService recreateOccurrenceService;

    private final Set<UUID> modifiedOccurrences = new HashSet<>();
    private final Set<UUID> deletedOccurrences = new HashSet<>();

    private boolean occurrenceRecreationNeeded;
    private List<Occurrence> occurrences;

    @Builder
    public UpdateEventContext(
        @NonNull Event event,
        @NonNull EventDao eventDao,
        @NonNull OccurrenceDao occurrenceDao,
        @NonNull RecreateOccurrenceService recreateOccurrenceService
    ) {
        this.event = event;
        this.eventDao = eventDao;
        this.occurrenceDao = occurrenceDao;
        this.recreateOccurrenceService = recreateOccurrenceService;
    }

    public List<Occurrence> getOccurrences() {
        if (isNull(occurrences)) {
            occurrences = occurrenceDao.getByEventId(event.getEventId());
        }

        return occurrences;
    }

    public void processChanges() {
        if (occurrenceRecreationNeeded) {
            recreateOccurrenceService.recreateOccurrences(this);
        }

        log.info("Saving event {}", event.getEventId());
        eventDao.save(event);

        log.info("Deleting {} occurrences for event {}", deletedOccurrences.size(), event.getEventId());
        occurrenceDao.deleteAllById(deletedOccurrences);

        List<Occurrence> modifiedOccurrences = getOccurrences().stream()
            .filter(occurrence -> this.modifiedOccurrences.contains(occurrence.getOccurrenceId()))
            .toList();
        log.info("Saving {} modified occurrences for event {}", modifiedOccurrences.size(), event.getEventId());
        occurrenceDao.saveAll(modifiedOccurrences);
    }

    public void occurrenceRecreationNeeded() {
        occurrenceRecreationNeeded = true;
    }

    /**
     * Deletes occurrences that match the given predicate.
     */
    public void deleteOccurrences(Predicate<Occurrence> predicate) {
        occurrences.stream()
            .filter(predicate)
            .forEach(occurrence -> deletedOccurrences.add(occurrence.getOccurrenceId()));
        occurrences.removeIf(predicate);
    }

    public void addOccurrence(Occurrence occurrence) {
        occurrences.add(occurrence);
        modifiedOccurrences.add(occurrence.getOccurrenceId());
    }
}

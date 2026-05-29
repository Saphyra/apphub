package com.github.saphyra.apphub.service.feature.calendar.common.context;

import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEvent;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.deprecated_dao.DeprecatedOccurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.deprecated_dao.DeprecatedOccurrenceDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.RecreateOccurrenceService;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

@Slf4j
public class UpdateEventContext {
    @Getter
    private final DeprecatedEvent event;

    private final DeprecatedEventDao eventDao;
    private final DeprecatedOccurrenceDao occurrenceDao;
    private final RecreateOccurrenceService recreateOccurrenceService;

    private final Set<UUID> modifiedOccurrences = new HashSet<>();
    private final Set<UUID> deletedOccurrences = new HashSet<>();

    private boolean occurrenceRecreationNeeded;
    private final LazyLoadedField<List<DeprecatedOccurrence>> occurrences;

    @Builder
    public UpdateEventContext(
        @NonNull DeprecatedEvent event,
        @NonNull DeprecatedEventDao eventDao,
        @NonNull DeprecatedOccurrenceDao occurrenceDao,
        @NonNull RecreateOccurrenceService recreateOccurrenceService
    ) {
        this.event = event;
        this.eventDao = eventDao;
        this.occurrenceDao = occurrenceDao;
        this.recreateOccurrenceService = recreateOccurrenceService;
        this.occurrences = new LazyLoadedField<>(() -> new ArrayList<>(occurrenceDao.getByEventId(event.getEventId())));
    }

    public List<DeprecatedOccurrence> getOccurrences() {
        return occurrences.get();
    }

    @Transactional
    public void processChanges() {
        if (occurrenceRecreationNeeded) {
            recreateOccurrenceService.recreateOccurrences(this);
        }

        log.info("Saving event {}", event.getEventId());
        eventDao.save(event);

        log.info("Deleting {} occurrences for event {}", deletedOccurrences.size(), event.getEventId());
        occurrenceDao.deleteAllById(deletedOccurrences);

        List<DeprecatedOccurrence> modifiedOccurrences = occurrences.get()
            .stream()
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
    public void deleteOccurrences(Predicate<DeprecatedOccurrence> predicate) {
        occurrences.get()
            .stream()
            .filter(predicate)
            .forEach(occurrence -> deletedOccurrences.add(occurrence.getOccurrenceId()));
        occurrences.get()
            .removeIf(predicate);
    }

    public void addOccurrence(DeprecatedOccurrence occurrence) {
        occurrences.get().add(occurrence);
        modifiedOccurrences.add(occurrence.getOccurrenceId());
    }
}

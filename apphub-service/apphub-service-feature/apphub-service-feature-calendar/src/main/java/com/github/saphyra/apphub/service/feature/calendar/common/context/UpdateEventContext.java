package com.github.saphyra.apphub.service.feature.calendar.common.context;

import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.service.feature.calendar.common.dao.CommonCalendarDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.RecreateOccurrenceService;
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
    private final Event event;

    private final CommonCalendarDao commonCalendarDao;
    private final RecreateOccurrenceService recreateOccurrenceService;

    private final Set<UUID> modifiedOccurrences = new HashSet<>();
    private final Set<UUID> deletedOccurrences = new HashSet<>();

    private boolean occurrenceRecreationNeeded;
    private final LazyLoadedField<List<Occurrence>> occurrences;

    @Builder
    public UpdateEventContext(
        @NonNull Event event,
        @NonNull CommonCalendarDao commonCalendarDao,
        @NonNull RecreateOccurrenceService recreateOccurrenceService
    ) {
        this.event = event;
        this.commonCalendarDao = commonCalendarDao;
        this.recreateOccurrenceService = recreateOccurrenceService;
        this.occurrences = new LazyLoadedField<>(() -> new ArrayList<>(commonCalendarDao.getOccurrenceDao().getByEventId(event.getEventId())));
    }

    public List<Occurrence> getOccurrences() {
        return occurrences.get();
    }

    public void processChanges() {
        if (occurrenceRecreationNeeded) {
            recreateOccurrenceService.recreateOccurrences(this);
        }

        log.info("Saving event {}", event.getEventId());
        commonCalendarDao.getEventDao()
            .save(event);

        log.info("Deleting {} occurrences for event {}", deletedOccurrences.size(), event.getEventId());
        commonCalendarDao.getOccurrenceDao()
            .delete(event.getEventId(), deletedOccurrences);

        List<Occurrence> modifiedOccurrences = occurrences.get()
            .stream()
            .filter(occurrence -> this.modifiedOccurrences.contains(occurrence.getOccurrenceId()))
            .toList();
        log.info("Saving {} modified occurrences for event {}", modifiedOccurrences.size(), event.getEventId());
        commonCalendarDao.getOccurrenceDao()
            .save(modifiedOccurrences);
    }

    public void occurrenceRecreationNeeded() {
        occurrenceRecreationNeeded = true;
    }

    /**
     * Deletes occurrences that match the given predicate.
     */
    public void deleteOccurrences(Predicate<Occurrence> predicate) {
        occurrences.get()
            .stream()
            .filter(predicate)
            .forEach(occurrence -> deletedOccurrences.add(occurrence.getOccurrenceId()));
        occurrences.get()
            .removeIf(predicate);
    }

    public void addOccurrence(Occurrence occurrence) {
        occurrences.get().add(occurrence);
        modifiedOccurrences.add(occurrence.getOccurrenceId());
    }

    public void processChanges(List<UUID> labels) {
        processChanges();

        commonCalendarDao.editLabelsOfEvent(event.getUserId(), event.getEventId(), labels);
    }
}

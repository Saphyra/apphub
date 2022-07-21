package com.github.saphyra.apphub.service.diary.service.occurrence.service.query;

import com.github.saphyra.apphub.service.diary.dao.occurance.Occurrence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
class OneTimeEventHandler {
    List<Occurrence> handleOneTimeEvent(List<Occurrence> occurrences, List<LocalDate> dates) {
        Occurrence occurrence = occurrences.get(0);
        if (dates.contains(occurrence.getDate())) {
            return List.of(occurrence);
        } else {
            return Collections.emptyList();
        }
    }
}

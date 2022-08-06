package com.github.saphyra.apphub.service.diary.service.occurrence.service.query;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.diary.dao.event.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class DaysOfMonthParser {
    private final ObjectMapperWrapper objectMapperWrapper;

    List<Integer> parseDaysOfMonth(Event event) {
        return objectMapperWrapper.readArrayValue(event.getRepetitionData(), Integer[].class);
    }
}

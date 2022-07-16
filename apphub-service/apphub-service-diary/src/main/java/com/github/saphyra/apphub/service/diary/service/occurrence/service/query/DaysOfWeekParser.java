package com.github.saphyra.apphub.service.diary.service.occurrence.service.query;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.diary.dao.event.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
//TODO unit test
public class DaysOfWeekParser {
    private final ObjectMapperWrapper objectMapperWrapper;

    List<DayOfWeek> parseDaysOfWeek(Event event) {
        return objectMapperWrapper.readArrayValue(event.getRepetitionData(), DayOfWeek[].class);
    }
}

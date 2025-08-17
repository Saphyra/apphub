package com.github.saphyra.apphub.service.calendar.domain.occurrence.service.condition;

import com.github.saphyra.apphub.api.calendar.model.RepetitionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RepetitionTypeConditionSelector {
    private final List<RepetitionTypeConditionFactory> repetitionTypeConditionFactories;

    public RepetitionTypeCondition get(RepetitionType repetitionType, Object repetitionData) {
        return repetitionTypeConditionFactories.stream()
            .filter(factory -> factory.getRepetitionType() == repetitionType)
            .findFirst()
            .orElseThrow(() -> new UnsupportedOperationException("Unhandled repetitionType " + repetitionType))
            .create(repetitionData);
    }
}
